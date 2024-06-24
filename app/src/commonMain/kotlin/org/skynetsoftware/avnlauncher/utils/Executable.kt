package org.skynetsoftware.avnlauncher.utils

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.domain.executable.ExecutableFinder
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.domain.utils.OS
import org.skynetsoftware.avnlauncher.domain.utils.os
import org.skynetsoftware.avnlauncher.logger.Logger
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.extension

val executableFinderKoinModule = module {
    single<ExecutableFinder> { ExecutableFinderDesktop(get(), get()) }
}

private class ExecutableFinderDesktop(
    val settingsRepository: SettingsRepository,
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
                    logger.debug("No executable found for '${game.title}'")
                }
            } else {
                val updatedExecutables = removeNonExistingExecutables(executablePaths)
                if (updatedExecutables.size != executablePaths.size) {
                    gamesToUpdate.add(game.f95ZoneThreadId to updatedExecutables)
                }
            }
        }
        return gamesToUpdate
    }

    private fun removeNonExistingExecutables(executablePaths: Set<String>): Set<String> {
        val mutableExecutablePaths = executablePaths.toMutableSet()
        mutableExecutablePaths.removeAll {
            !File(it).exists()
        }
        return mutableExecutablePaths
    }

    private fun getGamesRootDir(): File? {
        val gamesDir = settingsRepository.gamesDir
        return gamesDir.value?.let { File(it) }
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
            ?.filter { it.isDirectory }?.firstOrNull { it.name == title }?.let {
                Files.walk(Paths.get(it.absolutePath)).use { files ->
                    files.filter { f -> platformFilter(f) }.toList().mapNotNull { it.toFile().absolutePath }.toSet()
                }
            }.orEmpty()
    }

    private fun platformFilter(path: Path): Boolean {
        return when (os) {
            OS.Linux -> path.extension == "sh"
            OS.Windows -> path.extension == "exe"
            OS.Mac -> path.extension == "app"
        }
    }
}
