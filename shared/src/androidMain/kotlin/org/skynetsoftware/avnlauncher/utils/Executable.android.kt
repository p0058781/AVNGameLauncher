package org.skynetsoftware.avnlauncher.utils

import android.app.Application
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.logging.Logger
import java.io.File

actual val executableFinderKoinModule = module {
    single<ExecutableFinder> { ExecutableFinderAndroid(androidApplication(), get()) }
}

private class ExecutableFinderAndroid(
    private val application: Application,
    private val logger: Logger,
) : ExecutableFinder {
    override fun validateExecutables(games: List<Game>): List<Pair<Game, String>> {
        val gamesToUpdate = ArrayList<Pair<Game, String>>()
        games.forEach { game ->
            if (game.executablePath.isNullOrEmpty()) {
                val executable = findExecutable(game.title)
                if (executable != null) {
                    gamesToUpdate.add(game to executable)
                } else {
                    logger.warning("No executable found for '${game.title}'")
                }
            } else {
                val executablePathFile = File(game.executablePath)
                if (!executablePathFile.exists()) {
                    gamesToUpdate.add(game to "")
                }
            }
        }
        return gamesToUpdate
    }

    override fun findExecutable(title: String): String? {
        return application.packageManager.getInstalledApplications(0).find { it.name == title }?.packageName
    }
}
