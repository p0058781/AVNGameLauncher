package org.skynetsoftware.avnlauncher.data

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.model.toGame
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.f95.F95Api
import org.skynetsoftware.avnlauncher.utils.ExecutableFinder
import org.skynetsoftware.avnlauncher.utils.Result

val gameImportKoinModule = module {
    single<GameImport> {
        val configManager = get<ConfigManager>()
        if (configManager.remoteClientMode) {
            GameImportNoOp()
        } else {
            GameImportImpl(get(), get(), get())
        }
    }
}

// TODO option to add non-f95 games
interface GameImport {
    suspend fun importGame(threadId: Int): Result<Game>
}

private class GameImportNoOp : GameImport {
    override suspend fun importGame(threadId: Int): Result<Game> {
        throw IllegalStateException("not supported")
    }
}

private class GameImportImpl(
    private val gamesRepository: GamesRepository,
    private val f95Api: F95Api,
    private val executableFinder: ExecutableFinder,
) : GameImport {
    override suspend fun importGame(threadId: Int): Result<Game> {
        return when (val f95GameResult = f95Api.getGame(threadId)) {
            is Result.Error -> Result.Error(f95GameResult.exception)
            is Result.Ok -> {
                val game = f95GameResult.value.toGame().run {
                    val executablePath = executableFinder.findExecutables(f95GameResult.value.title)
                    if (executablePath.isEmpty()) {
                        this
                    } else {
                        this.copy(executablePaths = executablePath)
                    }
                }
                gamesRepository.insertGame(game)
                Result.Ok(game)
            }
        }
    }
}
