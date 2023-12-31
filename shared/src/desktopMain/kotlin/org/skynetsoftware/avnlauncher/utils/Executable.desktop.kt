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
    override fun validateExecutables(games: List<Game>): List<Pair<Game, String>> {
        val gamesRootDir = getGamesRootDir() ?: return emptyList()
        val gamesToUpdate = ArrayList<Pair<Game, String>>()
        games.forEach { game ->
            if (game.executablePath.isNullOrEmpty()) {
                val executable = findExecutable(gamesRootDir, game.title)
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

    private fun getGamesRootDir(): File? {
        val gamesDir = settingsManager.gamesDir
        if (configManager.remoteClientMode || gamesDir !is Option.Some) {
            return null
        }
        return gamesDir.value.value?.let { File(it) } ?: return null
    }

    override fun findExecutable(title: String): String? {
        val gamesRootDir = getGamesRootDir() ?: return null
        return findExecutable(gamesRootDir, title)
    }

    @Suppress("NewApi")
    private fun findExecutable(
        gamesDirRoot: File,
        title: String,
    ): String? {
        return gamesDirRoot.listFiles()
            ?.filter { it.isDirectory }
            ?.firstOrNull { it.name == title }?.let {
                Files.walk(Paths.get(it.absolutePath)).use { files ->
                    files
                        .filter { f -> platformFilter(f) }
                        .findFirst().getOrNull()?.toFile()
                }
            }?.absolutePath
    }

    private fun platformFilter(path: Path): Boolean {
        return when (os) {
            OS.Linux -> path.extension == "sh"
            OS.Windows -> path.extension == "exe"
            OS.Mac -> throw IllegalStateException("Mac is not supported")
        }
    }
}
