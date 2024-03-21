package org.skynetsoftware.avnlauncher.updatechecker

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.repository.F95Repository
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.domain.utils.Result
import org.skynetsoftware.avnlauncher.logger.Logger
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter
import kotlin.math.max

private const val UPDATE_CHECK_MAX_GAMES_IN_SINGLE_REQUEST = 100
private const val MIN_INTERVAL = 3_600_000L // 1h

val updateCheckerKoinModule = module {
    single<UpdateChecker> {
        UpdateCheckerImpl(get(), get(), get(), get(), get())
    }
}

abstract class UpdateChecker {
    abstract fun startPeriodicUpdateChecks()

    abstract fun stopPeriodicUpdateChecks()

    abstract fun checkForUpdates()

    abstract suspend fun checkForUpdates(scope: CoroutineScope): UpdateCheckResult
}

@Suppress("LongParameterList")
private class UpdateCheckerImpl(
    private val gamesRepository: GamesRepository,
    private val logger: Logger,
    private val f95Repository: F95Repository,
    private val eventCenter: EventCenter,
    private val settingsRepository: SettingsRepository,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : UpdateChecker() {
    private var updateCheckRunning = false
    private val scope = CoroutineScope(SupervisorJob() + coroutineDispatcher)

    private var updateCheckSchedulerJob: Job? = null

    init {
        scope.launch {
            settingsRepository.updateCheckInterval.collect {
                if (updateCheckSchedulerJob != null && updateCheckSchedulerJob?.isActive == true) {
                    stopPeriodicUpdateChecks()
                    startPeriodicUpdateChecks()
                }
            }
        }
    }

    override fun startPeriodicUpdateChecks() {
        updateCheckSchedulerJob?.cancel()
        updateCheckSchedulerJob = scope.launch {
            val interval = settingsRepository.updateCheckInterval.value.run {
                if (this < MIN_INTERVAL) {
                    this
                } else {
                    logger.info("updateCheckInterval is smaller then minimum value: $MIN_INTERVAL, using $MIN_INTERVAL")
                    MIN_INTERVAL
                }
            }
            logger.info("startPeriodicUpdateChecks interval: $interval")
            while (isActive) {
                val now = Clock.System.now().toEpochMilliseconds()
                val lastUpdateCheck = settingsRepository.lastUpdateCheck.value

                var lastUpdateCheckElapsedTime = now - lastUpdateCheck

                if (lastUpdateCheckElapsedTime > interval) {
                    checkForUpdates(this)
                    lastUpdateCheckElapsedTime = 0
                }

                val delay = interval - max(0, lastUpdateCheckElapsedTime)
                delay(delay)
                checkForUpdates(this)
            }
        }
    }

    override fun stopPeriodicUpdateChecks() {
        updateCheckSchedulerJob?.cancel()
        logger.info("stopPeriodicUpdateChecks")
    }

    override fun checkForUpdates() {
        scope.launch {
            checkForUpdates(this)
        }
    }

    override suspend fun checkForUpdates(scope: CoroutineScope): UpdateCheckResult {
        return runIfNotAlreadyRunning {
            eventCenter.emit(Event.UpdateCheckStarted)
            val start = Clock.System.now().toEpochMilliseconds()
            val allGames = gamesRepository.all().filter { it.checkForUpdates }
            logger.info("Checking ${allGames.size} games for updates")
            val updateCheckResults = checkForUpdates(allGames)
            logger.info(
                "Update check took ${Clock.System.now().toEpochMilliseconds() - start}ms," +
                    " made ${updateCheckResults.size} requests",
            )
            val gamesToUpdate = ArrayList<Game>()
            val exceptions = ArrayList<Throwable>()
            updateCheckResults.forEach {
                gamesToUpdate.addAll(it.updates)
                exceptions.addAll(it.exceptions)
            }
            if (gamesToUpdate.isEmpty()) {
                logger.info("No Updates Available")
            }
            logger.info("Updating ${gamesToUpdate.size} games")
            val updateResult = gamesToUpdate.map {
                scope.async {
                    updateGameFromF95(it)
                }
            }.map { it.await() }

            val gamesWithUpdate = ArrayList<Game>()
            updateResult.forEach {
                when (it) {
                    is Result.Error -> exceptions.add(it.exception)
                    is Result.Ok -> gamesWithUpdate.add(it.value)
                }
            }

            val mergedResult = UpdateCheckResult(
                updates = gamesWithUpdate,
                exceptions = exceptions,
            )
            gamesRepository.updateGames(gamesWithUpdate)
            eventCenter.emit(Event.UpdateCheckComplete(mergedResult))
            settingsRepository.setLastUpdateCheck(Clock.System.now().toEpochMilliseconds())
            mergedResult
        }
    }

    private suspend fun checkForUpdates(games: List<Game>): List<UpdateCheckResult> {
        return games.chunked(UPDATE_CHECK_MAX_GAMES_IN_SINGLE_REQUEST).map { gamesChunk ->
            scope.async {
                val versions = f95Repository.getVersions(gamesChunk.map { it.f95ZoneThreadId })
                when (versions) {
                    is Result.Error -> UpdateCheckResult(
                        updates = emptyList(),
                        exceptions = listOf(versions.exception),
                    )

                    is Result.Ok -> {
                        val gamesWithUpdates = gamesChunk.filter {
                            val remoteVersion = versions.value[it.f95ZoneThreadId] ?: false
                            it.version != remoteVersion && it.availableVersion != remoteVersion
                        }
                        UpdateCheckResult(
                            updates = gamesWithUpdates,
                            exceptions = emptyList(),
                        )
                    }
                }
            }
        }.map {
            it.await()
        }
    }

    private suspend fun updateGameFromF95(game: Game): Result<Game> {
        val currentVersion = game.version
        logger.info("Updating game data from F95: ${game.title}")
        return when (val f95Game = f95Repository.getGame(game.f95ZoneThreadId)) {
            is Result.Error -> Result.Error(f95Game.exception)
            is Result.Ok -> {
                var newGame = game.mergeWith(f95Game.value)

                val newVersion = f95Game.value.version
                if (newVersion != currentVersion) {
                    newGame = newGame.copy(updateAvailable = true, availableVersion = newVersion)
                    logger.info("${game.title}: Update Available $newVersion")
                }

                return Result.Ok(newGame)
            }
        }
    }

    private suspend fun runIfNotAlreadyRunning(run: suspend () -> UpdateCheckResult): UpdateCheckResult {
        if (updateCheckRunning) {
            return UpdateCheckResult(emptyList(), emptyList())
        }
        updateCheckRunning = true
        val result = run()
        updateCheckRunning = false
        return result
    }
}

private fun Game.mergeWith(f95Game: Game) =
    Game(
        title = f95Game.title,
        imageUrl = f95Game.imageUrl,
        customImageUrl = customImageUrl,
        f95ZoneThreadId = f95Game.f95ZoneThreadId,
        executablePaths = executablePaths,
        version = version,
        playTime = playTime,
        rating = rating,
        f95Rating = f95Game.f95Rating,
        updateAvailable = updateAvailable,
        added = added,
        lastPlayed = lastPlayed,
        hidden = hidden,
        releaseDate = f95Game.releaseDate,
        firstReleaseDate = f95Game.firstReleaseDate,
        playState = playState,
        availableVersion = availableVersion,
        tags = f95Game.tags,
        checkForUpdates = checkForUpdates,
        firstPlayed = firstPlayed,
        notes = notes,
        favorite = favorite,
    )
