package org.skynetsoftware.avnlauncher.launcher

import kotlinx.coroutines.runBlocking
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.logging.Logger
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.utils.OS
import org.skynetsoftware.avnlauncher.utils.format
import org.skynetsoftware.avnlauncher.utils.os

actual val gameLauncherKoinModule = module {
    single<GameLauncher> {
        val configManager = get<ConfigManager>()
        if (configManager.remoteClientMode) {
            GameLauncherNoOp()
        } else {
            GameLauncherDesktop(get(), get(), get())
        }
    }
}

private class GameLauncherDesktop(
    private val repository: GamesRepository,
    private val logger: Logger,
    private val eventCenter: EventCenter,
) : GameLauncher {
    private var processStarterThread: ProcessStarterThread? = null

    override fun launch(game: Game) {
        if (processStarterThread?.running == true) {
            val message = R.strings.gameLauncherAnotherGameRunning.format(game.title, processStarterThread?.game?.title)
            logger.warning(message)
            eventCenter.emit(Event.ToastMessage(message))
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
                val message = R.strings.gameLauncherInvalidExecutableToast.format(game.title)
                logger.info(message)
                eventCenter.emit(Event.ToastMessage(message))
                return
            }
            try {
                eventCenter.emit(Event.PlayingStarted(game))
                logger.info("game starting: ${game.title}")
                running = true
                val process = ProcessBuilder(*createCommand(game.executablePath)).apply {
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
                logger.info(
                    "game exited: ${game.title}, exit code: ${process.exitValue()} elapsedTime: $elapsedTime, totalTime: $totalTime",
                )
            } catch (t: Throwable) {
                logger.error(t)
            } finally {
                eventCenter.emit(Event.PlayingEnded)
                running = false
            }
        }

        private fun createCommand(executablePath: String): Array<String> {
            return when (os) {
                OS.Linux,
                OS.Windows,
                -> arrayOf(executablePath)

                OS.Mac -> throw IllegalStateException("Mac is not supported")
            }
        }
    }
}
