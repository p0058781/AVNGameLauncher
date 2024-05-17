package org.skynetsoftware.avnlauncher.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.Database
import org.skynetsoftware.avnlauncher.data.GameEntityQueries
import org.skynetsoftware.avnlauncher.data.mapper.toGame
import org.skynetsoftware.avnlauncher.data.mapper.toGameEntity
import org.skynetsoftware.avnlauncher.data.mapper.toGames
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlayState
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository

internal fun Module.gamesRepositoryKoinModule(coroutineDispatcher: CoroutineDispatcher) {
    single<GamesRepository> {
        GamesRepositoryImpl(get<Database>().gameEntityQueries, coroutineDispatcher)
    }
}

@Suppress("TooManyFunctions")
private class GamesRepositoryImpl(
    private val gameEntityQueries: GameEntityQueries,
    private val coroutineDispatcher: CoroutineDispatcher,
) : GamesRepository {
    override val games: Flow<List<Game>> =
        gameEntityQueries.gamesWithPlaySessions().asFlow().mapToList(coroutineDispatcher).map { it.toGames() }

    override suspend fun all(): List<Game> {
        return withContext(coroutineDispatcher) { gameEntityQueries.gamesWithPlaySessions().executeAsList().toGames() }
    }

    override suspend fun get(id: Int): Game? {
        return withContext(coroutineDispatcher) { gameEntityQueries.gameWithPlaySessions(id).executeAsList().toGame() }
    }

    override suspend fun updateRating(
        id: Int,
        rating: Int,
    ) {
        withContext(coroutineDispatcher) { gameEntityQueries.updateRating(rating, id) }
    }

    override suspend fun updateFavorite(
        id: Int,
        favorite: Boolean,
    ) {
        withContext(coroutineDispatcher) { gameEntityQueries.updateFavorite(favorite, id) }
    }

    override suspend fun updateExecutablePaths(games: List<Pair<Int, Set<String>>>) {
        withContext(coroutineDispatcher) {
            games.forEach {
                gameEntityQueries.updateExecutablePaths(it.second, it.first)
            }
        }
    }

    override suspend fun insertGame(game: Game) {
        withContext(coroutineDispatcher) { gameEntityQueries.insertGame(game.toGameEntity()) }
    }

    override suspend fun updateGames(games: List<Game>) {
        withContext(coroutineDispatcher) {
            games.forEach { game ->
                gameEntityQueries.updateGame(
                    title = game.title,
                    executablePaths = game.executablePaths,
                    imageUrl = game.imageUrl,
                    customImageUrl = null,
                    checkForUpdates = game.checkForUpdates,
                    version = game.version,
                    rating = game.rating,
                    f95Rating = game.f95Rating,
                    updateAvailable = game.updateAvailable,
                    added = game.added,
                    hidden = game.hidden,
                    releaseDate = game.releaseDate,
                    firstReleaseDate = game.firstReleaseDate,
                    playState = game.playState,
                    availableVersion = game.availableVersion,
                    tags = game.tags,
                    f95ZoneThreadId = game.f95ZoneThreadId,
                    notes = game.notes,
                    favorite = game.favorite,
                )
            }
        }
    }

    override suspend fun updateGame(
        id: Int,
        executablePaths: Set<String>,
        checkForUpdates: Boolean,
        playState: PlayState,
        hidden: Boolean,
        notes: String?,
    ) {
        withContext(coroutineDispatcher) {
            gameEntityQueries.updateGameF95(
                executablePaths,
                checkForUpdates,
                playState,
                hidden,
                notes,
                id,
            )
        }
    }

    override suspend fun updateGame(
        id: Int,
        title: String,
        imageUrl: String,
        version: String,
        releaseDate: Long,
        firstReleaseDate: Long,
        tags: Set<String>,
        executablePaths: Set<String>,
        checkForUpdates: Boolean,
        playState: PlayState,
        hidden: Boolean,
        notes: String?,
    ) {
        withContext(coroutineDispatcher) {
            gameEntityQueries.updateGameNonF95(
                title,
                imageUrl,
                version,
                releaseDate,
                firstReleaseDate,
                tags,
                executablePaths,
                checkForUpdates,
                playState,
                hidden,
                notes,
                id,
            )
        }
    }

    override suspend fun updateGame(
        id: Int,
        updateAvailable: Boolean,
        version: String,
        availableVersion: String?,
    ) {
        withContext(coroutineDispatcher) {
            gameEntityQueries.updateVersion(updateAvailable, version, availableVersion, id)
        }
    }
}
