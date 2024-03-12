package org.skynetsoftware.avnlauncher.launcher

import kotlinx.coroutines.runBlocking
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository
import org.skynetsoftware.avnlauncher.domain.utils.OS
import org.skynetsoftware.avnlauncher.domain.utils.os
import org.skynetsoftware.avnlauncher.logger.Logger
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter

actual val gameLauncherKoinModule = module {
    single<GameLauncher> {
        GameLauncherDesktop(get(), get(), get())
    }
}

private class GameLauncherDesktop(
    private val repository: GamesRepository,
    private val logger: Logger,
    private val eventCenter: EventCenter,
) : GameLauncher {
    private var processStarterThread: ProcessStarterThread? = null

    override fun launch(
        game: Game,
        executablePath: String,
    ) {
        if (processStarterThread?.running == true) {
            eventCenter.emit(
                Event.ToastMessage(
                    message = MR.strings.gameLauncherAnotherGameRunning,
                    game.title,
                    processStarterThread?.game?.title.orEmpty(),
                ),
            )
            return
        }

        processStarterThread = ProcessStarterThread(game, executablePath, repository, logger, eventCenter)
        processStarterThread?.start()
    }

    private class ProcessStarterThread(
        val game: Game,
        private val executablePath: String,
        private val repository: GamesRepository,
        private val logger: Logger,
        private val eventCenter: EventCenter,
    ) : Thread() {
        var running = false
            private set

        @Suppress("NewApi")
        override fun run() {
            try {
                eventCenter.emit(Event.PlayingStarted(game))
                logger.info("game starting: ${game.title}")
                running = true
                val process = ProcessBuilder(createCommand(executablePath)).apply {
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
                    repository.updateGame(game.f95ZoneThreadId, totalTime, System.currentTimeMillis())
                }
                logger.info(
                    "game exited: ${game.title}, " +
                        "exit code: ${process.exitValue()} elapsedTime: $elapsedTime, totalTime: $totalTime",
                )
            } catch (t: Throwable) {
                logger.error(t)
                t.message?.let {
                    eventCenter.emit(Event.ToastMessage(it))
                }
            } finally {
                eventCenter.emit(Event.PlayingEnded)
                running = false
            }
        }

        private fun createCommand(executablePath: String): List<String> {
            return when (os) {
                OS.Linux,
                OS.Windows,
                -> listOf(executablePath)
                OS.Mac -> listOf("open", "-W", executablePath)
            }
        }
    }
}
