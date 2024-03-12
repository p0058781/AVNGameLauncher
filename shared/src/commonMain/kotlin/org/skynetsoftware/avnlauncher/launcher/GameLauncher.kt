package org.skynetsoftware.avnlauncher.launcher

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.logging.Logger
import org.skynetsoftware.avnlauncher.utils.OS
import org.skynetsoftware.avnlauncher.utils.os

val gameLauncherKoinModule = module {
    single<GameLauncher> { GameLauncherImpl(get(), get()) }
}

interface GameLauncher {
    fun launch(game: Game)
}

private class GameLauncherImpl(private val repository: GamesRepository, private val logger: Logger) : GameLauncher {

    private var processStarterThread: ProcessStarterThread? = null

    override fun launch(game: Game) {
        if (processStarterThread?.running == true) {
            logger.warning("cant start game: ${game.title}, game: ${processStarterThread?.game?.title} is already running")
            //game is already running
            return
        }

        processStarterThread = ProcessStarterThread(game, repository, logger)
        processStarterThread?.start()
    }

    private class ProcessStarterThread(val game: Game, private val repository: GamesRepository, private val logger: Logger) : Thread() {
        var running = false
            private set

        override fun run() {
            try {
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
                val totalTime = (game.playTime ?: 0L) + elapsedTime
                repository.updatePlayTime(totalTime, game.id)
                repository.updateLastPlayed(System.currentTimeMillis(), game.id)
                logger.info("game exited: ${game.title}, exit code: ${process.exitValue()} elapsedTime: $elapsedTime, totalTime: $totalTime")
            } catch (t: Throwable) {
                t.printStackTrace()
            } finally {
                running = false
            }

        }

        private fun createCommand(executablePath: String): String {
            return when(os) {
                OS.Linux,
                OS.Windows -> executablePath
                OS.Mac -> "open $executablePath"
            }
        }
    }
}