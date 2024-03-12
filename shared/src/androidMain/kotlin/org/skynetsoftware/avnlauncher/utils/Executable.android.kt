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
    override fun validateExecutables(games: List<Game>): List<Pair<Int, Set<String>>> {
        val gamesToUpdate = ArrayList<Pair<Int, Set<String>>>()
        games.forEach { game ->
            val executablePaths = game.executablePaths
            if (executablePaths.isEmpty()) {
                val executables = findExecutables(game.title)
                if (executables.isNotEmpty()) {
                    gamesToUpdate.add(game.f95ZoneThreadId to executables)
                } else {
                    logger.warning("No executable found for '${game.title}'")
                }
            } else {
                val mutableExecutablePaths = executablePaths.toMutableSet()
                mutableExecutablePaths.forEach {
                    val executablePathFile = File(it)
                    if (!executablePathFile.exists()) {
                        mutableExecutablePaths.remove(it)
                    }
                }
                if (mutableExecutablePaths.size != executablePaths.size) {
                    gamesToUpdate.add(game.f95ZoneThreadId to mutableExecutablePaths)
                }
            }
        }
        return gamesToUpdate
    }

    override fun findExecutables(title: String): Set<String> {
        return application.packageManager.getInstalledApplications(0).filter { it.name == title }.map { it.packageName }.toSet()
    }
}
