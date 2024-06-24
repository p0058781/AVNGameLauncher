package org.skynetsoftware.avnlauncher.domain.usecase

import org.skynetsoftware.avnlauncher.domain.executable.ExecutableFinder
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.repository.F95Repository
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository
import org.skynetsoftware.avnlauncher.domain.utils.Result

class GameExistsException : Exception()

class InvalidUrlException : Exception()

class ImportGameUseCase(
    private val gamesRepository: GamesRepository,
    private val f95Repository: F95Repository,
    private val executableFinder: ExecutableFinder,
) {
    private val gameThreadUrlRegex = Regex("https://f95zone.to/threads/.+\\.(\\d+).*")

    suspend operator fun invoke(
        threadIdOrUrl: String,
        playTime: Long?,
        firstPlayed: Long?,
    ): Result<Game> {
        var threadId = threadIdOrUrl.toIntOrNull()
        if (threadId == null) {
            threadId = gameThreadUrlRegex.matchEntire(threadIdOrUrl)?.groupValues?.getOrNull(1)?.toIntOrNull()
                ?: return Result.Error(InvalidUrlException())
        }
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
                        this.copy(totalPlayTime = playTime)
                    }
                }.run {
                    if (firstPlayed == null) {
                        this
                    } else {
                        this.copy(firstPlayedTime = firstPlayed)
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
