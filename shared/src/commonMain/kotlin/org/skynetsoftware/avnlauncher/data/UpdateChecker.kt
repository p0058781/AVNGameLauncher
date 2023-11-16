package org.skynetsoftware.avnlauncher.data

import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.f95.F95Api
import org.skynetsoftware.avnlauncher.logging.Logger

private const val UPDATE_CHECK_INTERVAL = 86_400_000L//24h

val updateCheckerKoinModule = module {
    single<UpdateChecker> { UpdateCheckerImpl(get(), get(), get()) }
}

interface UpdateChecker {

    class UpdateResult(
        val game: Game,
        val updateAvailable: Boolean,
        val exception: Throwable?
    )

    fun startUpdateCheck(forceUpdateCheck: Boolean = false, onComplete: (updateResults: List<UpdateResult>) -> Unit)
}

private class UpdateCheckerImpl(
    private val gamesRepository: GamesRepository,
    private val logger: Logger,
    private val f95Api: F95Api
) : UpdateChecker {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var updateCheckRunning = false

    override fun startUpdateCheck(
        forceUpdateCheck: Boolean,
        onComplete: (updateResults: List<UpdateChecker.UpdateResult>) -> Unit
    ) {
        scope.launch {
            runIfNotAlreadyRunning {
                val now = Clock.System.now().toEpochMilliseconds()
                val games = gamesRepository.all()
                    .filter { forceUpdateCheck || now > it.lastUpdateCheck + UPDATE_CHECK_INTERVAL }
                    .filter { !it.updateAvailable }
                val updatesResult = games.map { game ->
                    async {
                        try {
                            val currentVersion = game.version

                            val f95Game = f95Api.getGame(game.f95ZoneThreadId).getOrThrow()
                            //TODO update RealmGame with new data, merge with existing
                            val newVersion = f95Game.version
                            val releaseDate = f95Game.releaseDate
                            if (newVersion != currentVersion) {
                                gamesRepository.updateUpdateAvailable(true, game)
                                gamesRepository.updateAvailableVersion(newVersion, game)
                                logger.info("${game.title}: Update Available $newVersion")
                            }

                            if (releaseDate != game.releaseDate) {
                                gamesRepository.updateReleaseDate(releaseDate, game)
                            }

                            gamesRepository.updateLastUpdateCheck(now, game)
                            UpdateChecker.UpdateResult(game, newVersion != currentVersion, null)
                        } catch (t: Throwable) {
                            logger.error(t)
                            UpdateChecker.UpdateResult(game, false, t)
                        }
                    }
                }.map { it.await() }
                if (updatesResult.none { it.updateAvailable }) {
                    logger.info("No Updates Available")
                }
                onComplete(updatesResult)
            }
        }
    }

    private suspend fun runIfNotAlreadyRunning(run: suspend () -> Unit) {
        if (updateCheckRunning) {
            return
        }
        updateCheckRunning = true
        run()
        updateCheckRunning = false
    }
}