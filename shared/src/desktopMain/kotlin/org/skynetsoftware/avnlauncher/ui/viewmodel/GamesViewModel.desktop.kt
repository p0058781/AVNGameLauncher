package org.skynetsoftware.avnlauncher.ui.viewmodel

import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.logging.Logger
import org.skynetsoftware.avnlauncher.settings.SettingsManager
import org.skynetsoftware.avnlauncher.utils.OS
import org.skynetsoftware.avnlauncher.utils.os
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.extension
import kotlin.jvm.optionals.getOrNull

actual suspend fun validateExecutables(
    gamesRepository: GamesRepository,
    settingsManager: SettingsManager,
    configManager: ConfigManager,
    logger: Logger,
) {
    if (configManager.remoteClientMode) {
        return
    }
    val gamesDirRoot = settingsManager.gamesDir.value?.let { File(it) } ?: return
    val allGames = gamesRepository.all()
    allGames.forEach { game ->
        if (game.executablePath.isNullOrEmpty()) {
            val executable = findExecutable(gamesDirRoot, game)
            if (executable != null) {
                gamesRepository.updateExecutablePath(executable, game)
            } else {
                logger.warning("No executable found for '${game.title}'")
            }
        } else {
            val executablePathFile = File(game.executablePath)
            if (!executablePathFile.exists()) {
                gamesRepository.updateExecutablePath("", game)
            }
        }
    }
}

@Suppress("NewApi")
private fun findExecutable(
    gamesDirRoot: File,
    game: Game,
): String? {
    return gamesDirRoot.listFiles()
        ?.filter { it.isDirectory }
        ?.firstOrNull { it.name == game.title }?.let {
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
        OS.Mac -> TODO()
    }
}
