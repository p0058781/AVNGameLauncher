package org.skynetsoftware.avnlauncher.data

import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.model.convertF95GameAndV1GameToGame
import org.skynetsoftware.avnlauncher.data.model.legacy.V1Game
import org.skynetsoftware.avnlauncher.data.model.toGame
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.f95.F95Api
import org.skynetsoftware.avnlauncher.utils.readToString

val gameImportKoinModule = module {
    single<GameImport> { GameImportImpl(get(), get(), get()) }
}

interface GameImport {
    fun importGame(threadId: Int, onGameImported: (title: Result<String>) -> Unit)

    fun importGames(file: String, onGamesImported: (imported: Int) -> Unit)
}

private class GameImportImpl(
    configManager: ConfigManager,
    private val gamesRepository: GamesRepository,
    private val f95Api: F95Api
) : GameImport {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    //private val gamesDir = configManager.gamesDir

    override fun importGame(threadId: Int, onGameImported: (title: Result<String>) -> Unit) {
        coroutineScope.launch {
            try {
                val f95Game = f95Api.getGame(threadId).getOrThrow()
                //TODO [medium] search executable/android app
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

    override fun importGames(file: String, onGamesImported: (imported: Int) -> Unit) {
        coroutineScope.launch {
            try {
                val importFileString = file.toPath().readToString()
                val v1Games = Json { ignoreUnknownKeys = true }.decodeFromString<List<V1Game>>(importFileString)
                val tasks: List<Deferred<Game>> = v1Games.map {
                    async {
                        try {
                            val f95Game = f95Api.getGame(it.f95ZoneUrl).getOrThrow()
                            convertF95GameAndV1GameToGame(f95Game, it)
                        } catch (e: Throwable) {
                            it.toGame()
                        }
                    }
                }


                val result = tasks.awaitAll()
                result.forEach {
                    gamesRepository.insertGame(it)
                }
                onGamesImported(result.size)
            } catch (t: Throwable) {
                t.printStackTrace()
                onGamesImported(0)
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