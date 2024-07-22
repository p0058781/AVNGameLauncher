package org.skynetsoftware.avnlauncher.launcher

import kotlinx.coroutines.runBlocking
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.gameLauncherAnotherGameRunning
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlaySession
import org.skynetsoftware.avnlauncher.domain.repository.PlaySessionRepository
import org.skynetsoftware.avnlauncher.domain.utils.OS
import org.skynetsoftware.avnlauncher.domain.utils.os
import org.skynetsoftware.avnlauncher.logger.Logger
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter

val gameLauncherKoinModule = module {
    single<GameLauncher> {
        GameLauncherDesktop(get(), get(), get())
    }
}

interface GameLauncher {
    fun launch(
        game: Game,
        executablePath: String,
    )

    fun stop()
}

private class GameLauncherDesktop(
    private val playSessionRepository: PlaySessionRepository,
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
                    message = Res.string.gameLauncherAnotherGameRunning,
                    game.title,
                    processStarterThread?.game?.title.orEmpty(),
                ),
            )
            return
        }

        processStarterThread = ProcessStarterThread(game, executablePath, playSessionRepository, logger, eventCenter)
        processStarterThread?.start()
    }

    override fun stop() {
        processStarterThread?.stopGame()
    }

    private class ProcessStarterThread(
        val game: Game,
        private val executablePath: String,
        private val playSessionRepository: PlaySessionRepository,
        private val logger: Logger,
        private val eventCenter: EventCenter,
    ) : Thread() {
        var running = false
            private set
        private lateinit var process: Process

        @Suppress("NewApi")
        override fun run() {
            try {
                eventCenter.emit(Event.PlayingStarted(game))
                logger.info("game starting: ${game.title}")
                running = true
                process = ProcessBuilder(createCommand(executablePath)).apply {
                    redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    redirectError(ProcessBuilder.Redirect.DISCARD)
                }.start()

                val startTime = System.currentTimeMillis()

                while (process.isAlive) {
                    runCatching {
                        process.waitFor()
                    }
                }
                val endTime = System.currentTimeMillis()
                runBlocking {
                    playSessionRepository.insertPlaySession(
                        PlaySession(
                            gameId = game.f95ZoneThreadId,
                            startTime = startTime,
                            endTime = endTime,
                            version = executablePath,
                        ),
                    )
                }
                logger.info(
                    "game exited: ${game.title}, " +
                        "exit code: ${process.exitValue()} elapsedTime: ${endTime - startTime}",
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

        fun stopGame() {
            if (running) {
                process.destroy()
                interrupt()
            }
        }
    }
}
