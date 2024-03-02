package org.skynetsoftware.avnlauncher.updatechecker

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.repository.F95Repository
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository
import org.skynetsoftware.avnlauncher.domain.utils.Result
import org.skynetsoftware.avnlauncher.domain.utils.valueOrNull
import org.skynetsoftware.avnlauncher.logger.Logger
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter
import kotlin.math.max

val updateCheckerKoinModule = module {
    single<UpdateChecker> {
        UpdateCheckerImpl(get(), get(), get(), get(), get())
    }
}

abstract class UpdateChecker(private val configManager: ConfigManager) {
    abstract fun startPeriodicUpdateChecks(interval: Long = configManager.updateCheckInterval)

    abstract fun stopPeriodicUpdateChecks()

    abstract suspend fun checkForUpdates(
        scope: CoroutineScope,
        forceUpdateCheck: Boolean = false,
    ): UpdateCheckResult
}

@Suppress("LongParameterList")
private class UpdateCheckerImpl(
    private val gamesRepository: GamesRepository,
    private val logger: Logger,
    private val f95Repository: F95Repository,
    private val eventCenter: EventCenter,
    private val configManager: ConfigManager,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : UpdateChecker(configManager) {
    private var updateCheckRunning = false
    private val scope = CoroutineScope(SupervisorJob() + coroutineDispatcher)

    private var updateCheckSchedulerJob: Job? = null

    override fun startPeriodicUpdateChecks(interval: Long) {
        updateCheckSchedulerJob?.cancel()
        updateCheckSchedulerJob = scope.launch {
            while (isActive) {
                val now = Clock.System.now().toEpochMilliseconds()
                val newestLastUpdateCheck = gamesRepository.all()
                    .takeIf { it.isNotEmpty() }?.maxOf { it.lastUpdateCheck } ?: now

                var lastUpdateCheckElapsedTime = now - newestLastUpdateCheck

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
    }

    override suspend fun checkForUpdates(
        scope: CoroutineScope,
        forceUpdateCheck: Boolean,
    ): UpdateCheckResult {
        return runIfNotAlreadyRunning {
            eventCenter.emit(Event.UpdateCheckStarted)
            val now = Clock.System.now().toEpochMilliseconds()
            val games = gamesRepository.all()
                .filter { forceUpdateCheck || now > it.lastUpdateCheck + configManager.updateCheckInterval }
                .filter { it.checkForUpdates }
            val updatesResult = games.map { game ->
                scope.async {
                    val lastRedirectUrl = game.lastRedirectUrl
                    var updatedRedirectUrl: String? = null

                    val newRedirectUrl = f95Repository.getRedirectUrl(game.f95ZoneThreadId).valueOrNull()
                    val result = if (newRedirectUrl != lastRedirectUrl) {
                        val slowResult = doSlowUpdateCheck(game)
                        newRedirectUrl?.let {
                            updatedRedirectUrl = it
                        }
                        slowResult
                    } else {
                        UpdateCheckGame(game, false, null)
                    }
                    val newGame = result.game.copy(
                        lastUpdateCheck = now,
                        lastRedirectUrl = if (updatedRedirectUrl == null) {
                            result.game.lastRedirectUrl
                        } else {
                            updatedRedirectUrl
                        },
                    )

                    result.copy(game = newGame)
                }
            }.map {
                it.await()
            }
            gamesRepository.updateGames(updatesResult.map { it.game })
            if (updatesResult.none { it.updateAvailable }) {
                logger.info("No Updates Available")
            }
            val result = UpdateCheckResult(updatesResult)
            eventCenter.emit(Event.UpdateCheckComplete(result))
            result
        }
    }

    private suspend fun doSlowUpdateCheck(game: Game): UpdateCheckGame {
        val currentVersion = game.version
        logger.info("doing slow update check for: ${game.title}")
        return when (val f95Game = f95Repository.getGame(game.f95ZoneThreadId)) {
            is Result.Error -> UpdateCheckGame(game, false, f95Game.exception)
            is Result.Ok -> {
                var newGame = game.mergeWith(f95Game.value)

                val newVersion = f95Game.value.version
                if (newVersion != currentVersion) {
                    newGame = newGame.copy(updateAvailable = true, availableVersion = newVersion)
                    logger.info("${game.title}: Update Available $newVersion")
                }

                return UpdateCheckGame(newGame, newVersion != currentVersion, null)
            }
        }
    }

    private suspend fun runIfNotAlreadyRunning(run: suspend () -> UpdateCheckResult): UpdateCheckResult {
        if (updateCheckRunning) {
            return UpdateCheckResult(emptyList())
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
        lastUpdateCheck = lastUpdateCheck,
        hidden = hidden,
        releaseDate = f95Game.releaseDate,
        firstReleaseDate = f95Game.firstReleaseDate,
        playState = playState,
        availableVersion = availableVersion,
        tags = f95Game.tags,
        lastRedirectUrl = lastRedirectUrl,
        checkForUpdates = checkForUpdates,
        firstPlayed = firstPlayed,
        notes = notes,
        favorite = favorite,
    )
