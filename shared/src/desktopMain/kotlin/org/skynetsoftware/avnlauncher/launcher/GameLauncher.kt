package org.skynetsoftware.avnlauncher.launcher

import kotlinx.coroutines.runBlocking
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.logging.Logger
import org.skynetsoftware.avnlauncher.settings.SettingsManager
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.utils.OS
import org.skynetsoftware.avnlauncher.utils.os

actual val gameLauncherKoinModule = module {
    single<GameLauncher> { GameLauncherDesktop(get(), get(), get(), get()) }
}

private class GameLauncherDesktop(
    private val repository: GamesRepository,
    private val logger: Logger,
    private val eventCenter: EventCenter,
    private val settingsManager: SettingsManager
) : GameLauncher {

    private var processStarterThread: ProcessStarterThread? = null

    override fun launch(game: Game) {
        if (settingsManager.remoteClientMode.value) {
            logger.info("cant start game in remote client mode")
            return
        }
        if (processStarterThread?.running == true) {
            //TODO show in UI
            logger.warning("cant start game: ${game.title}, game: ${processStarterThread?.game?.title} is already running")
            //game is already running
            return
        }

        processStarterThread = ProcessStarterThread(game, repository, logger, eventCenter)
        processStarterThread?.start()
    }

    private class ProcessStarterThread(
        val game: Game,
        private val repository: GamesRepository,
        private val logger: Logger,
        private val eventCenter: EventCenter,
    ) : Thread() {
        var running = false
            private set

        @Suppress("NewApi")
        override fun run() {
            if (game.executablePath.isNullOrBlank()) {
                logger.info("cant launch game, executablePath is invalid")
                return
            }
            try {
                eventCenter.emit(Event.PlayingStarted(game))
                logger.info("game starting: ${game.title}")
                running = true
                val process = ProcessBuilder(createCommand(game.executablePath)).apply {
                    redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    redirectError(ProcessBuilder.Redirect.DISCARD)
                }.start()

                val startTime = System.currentTimeMillis()

                while (process.isAlive) {
                    runCatching {
                        process.waitFor()
                    }
                }
                val elapsedTime = System.currentTimeMillis() - startTime
                val totalTime = game.playTime + elapsedTime
                runBlocking {
                    repository.updatePlayTime(totalTime, game)
                    repository.updateLastPlayed(System.currentTimeMillis(), game)
                }
                logger.info("game exited: ${game.title}, exit code: ${process.exitValue()} elapsedTime: $elapsedTime, totalTime: $totalTime")
            } catch (t: Throwable) {
                logger.error(t)
            } finally {
                eventCenter.emit(Event.PlayingEnded)
                running = false
            }

        }

        private fun createCommand(executablePath: String): String {
            return when (os) {
                OS.Linux,
                OS.Windows -> executablePath

                OS.Mac -> "open $executablePath"
            }
        }
    }
}