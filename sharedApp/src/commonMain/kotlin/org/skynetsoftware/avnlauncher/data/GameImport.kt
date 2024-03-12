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

interface GameImport {
    suspend fun importGame(
        threadId: Int,
        playTime: Long? = null,
        firstPlayed: Long? = null,
    ): Result<Game>

    suspend fun importGame(
        threadUrl: String,
        playTime: Long? = null,
        firstPlayed: Long? = null,
    ): Result<Game>

    class GameExistsException : Exception()

    class InvalidUrlException : Exception()
}

private class GameImportImpl(
    private val gamesRepository: GamesRepository,
    private val f95Repository: F95Repository,
    private val executableFinder: ExecutableFinder,
) : GameImport {
    private val gameThreadUrlRegex = Regex("https://f95zone.to/threads/.+l\\.(\\d+).*")

    override suspend fun importGame(
        threadId: Int,
        playTime: Long?,
        firstPlayed: Long?,
    ): Result<Game> {
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
                }.run {
                    if (playTime == null) {
                        this
                    } else {
                        this.copy(playTime = playTime)
                    }
                }.run {
                    if (firstPlayed == null) {
                        this
                    } else {
                        this.copy(firstPlayed = firstPlayed)
                    }
                }
                val existingGame = gamesRepository.get(game.f95ZoneThreadId)
                if (existingGame == null) {
                    gamesRepository.insertGame(game)
                    Result.Ok(game)
                } else {
                    Result.Error(GameImport.GameExistsException())
                }
            }
        }
    }

    override suspend fun importGame(
        threadUrl: String,
        playTime: Long?,
        firstPlayed: Long?,
    ): Result<Game> {
        val threadId = gameThreadUrlRegex.matchEntire(threadUrl)?.groupValues?.getOrNull(1)?.toIntOrNull()
            ?: return Result.Error(GameImport.InvalidUrlException())
        return importGame(threadId, playTime, firstPlayed)
    }
}
