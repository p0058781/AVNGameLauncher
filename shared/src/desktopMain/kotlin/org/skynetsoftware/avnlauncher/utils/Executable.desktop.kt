package org.skynetsoftware.avnlauncher.utils

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.logging.Logger
import org.skynetsoftware.avnlauncher.settings.SettingsManager
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.extension
import kotlin.jvm.optionals.getOrNull

actual val executableFinderKoinModule = module {
    single<ExecutableFinder> { ExecutableFinderDesktop(get(), get(), get()) }
}

private class ExecutableFinderDesktop(
    val settingsManager: SettingsManager,
    val configManager: ConfigManager,
    val logger: Logger,
) : ExecutableFinder {
    override fun validateExecutables(games: List<Game>): List<Pair<Int, Set<String>>> {
        val gamesRootDir = getGamesRootDir() ?: return emptyList()
        val gamesToUpdate = ArrayList<Pair<Int, Set<String>>>()
        games.forEach { game ->
            val executablePaths = game.executablePaths
            if (executablePaths.isEmpty()) {
                val executables = findExecutables(gamesRootDir, game.title)
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

    private fun getGamesRootDir(): File? {
        val gamesDir = settingsManager.gamesDir
        if (gamesDir !is Option.Some) {
            return null
        }
        return gamesDir.value.value?.let { File(it) } ?: return null
    }

    override fun findExecutables(title: String): Set<String> {
        val gamesRootDir = getGamesRootDir() ?: return emptySet()
        return findExecutables(gamesRootDir, title)
    }

    @Suppress("NewApi")
    private fun findExecutables(
        gamesDirRoot: File,
        title: String,
    ): Set<String> {
        return gamesDirRoot.listFiles()
            ?.filter { it.isDirectory }
            ?.filter { it.name == title }?.mapNotNull {
                Files.walk(Paths.get(it.absolutePath)).use { files ->
                    files
                        .filter { f -> platformFilter(f) }
                        .findFirst().getOrNull()?.toFile()
                }?.absolutePath
            }?.toSet() ?: emptySet()
    }

    private fun platformFilter(path: Path): Boolean {
        return when (os) {
            // TODO need to support more than just sh
            OS.Linux -> path.extension == "sh"
            OS.Windows -> path.extension == "exe"
            OS.Mac -> path.extension == "app"
        }
    }
}
