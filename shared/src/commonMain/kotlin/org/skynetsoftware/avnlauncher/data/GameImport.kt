package org.skynetsoftware.avnlauncher.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.model.toGame
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.f95.F95Api

val gameImportKoinModule = module {
    single<GameImport> {
        val configManager = get<ConfigManager>()
        if (configManager.remoteClientMode) {
            GameImportNoOp()
        } else {
            GameImportImpl(get(), get())
        }
    }
}

interface GameImport {
    fun importGame(
        threadId: Int,
        onGameImported: (title: Result<String>) -> Unit,
    )
}

private class GameImportNoOp : GameImport {
    override fun importGame(
        threadId: Int,
        onGameImported: (title: Result<String>) -> Unit,
    ) {}
}

private class GameImportImpl(
    private val gamesRepository: GamesRepository,
    private val f95Api: F95Api,
) : GameImport {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    // private val gamesDir = configManager.gamesDir

    override fun importGame(
        threadId: Int,
        onGameImported: (title: Result<String>) -> Unit,
    ) {
        coroutineScope.launch {
            try {
                val f95Game = f95Api.getGame(threadId).getOrThrow()
                // TODO [medium] search executable/android app

                /*val executable = gamesDir.listFiles()
                    ?.filter { it.isDirectory }
                    ?.firstOrNull { it.name.lowercase().contains(title.replace(" ", "").lowercase()) }?.let {
                        findExecutable(it.absolutePath)
                    }?.absolutePath ?: ""
                 */
                gamesRepository.insertGame(f95Game.toGame())
                onGameImported(Result.success(f95Game.title))
            } catch (t: Throwable) {
                onGameImported(Result.failure(t))
            }
        }
    }

    /*private fun findExecutable(searchDirectory: String): File? {
        Files.walk(Paths.get(searchDirectory)).use { files ->
            return files
                .filter { f -> f.extension == "sh" }
                .findFirst().getOrNull()?.toFile()
        }
    }*/
}
