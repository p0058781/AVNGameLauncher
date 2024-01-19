package org.skynetsoftware.avnlauncher.data

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.repository.F95Repository
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository
import org.skynetsoftware.avnlauncher.domain.utils.Result
import org.skynetsoftware.avnlauncher.utils.ExecutableFinder

val gameImportKoinModule = module {
    single<GameImport> {
        GameImportImpl(get(), get(), get())
    }
}

class GameExistsException : Exception()

interface GameImport {
    suspend fun importGame(threadId: Int): Result<Game>
}

private class GameImportImpl(
    private val gamesRepository: GamesRepository,
    private val f95Repository: F95Repository,
    private val executableFinder: ExecutableFinder,
) : GameImport {
    override suspend fun importGame(threadId: Int): Result<Game> {
        return when (val f95GameResult = f95Repository.getGame(threadId)) {
            is Result.Error -> Result.Error(f95GameResult.exception)
            is Result.Ok -> {
                val game = f95GameResult.value.run {
                    val executablePath = executableFinder.findExecutables(f95GameResult.value.title)
                    if (executablePath.isEmpty()) {
                        this
                    } else {
                        this.copy(executablePaths = executablePath)
                    }
                }
                val existingGame = gamesRepository.get(game.f95ZoneThreadId)
                if (existingGame == null) {
                    gamesRepository.insertGame(game)
                    Result.Ok(game)
                } else {
                    Result.Error(GameExistsException())
                }
            }
        }
    }
}
