package org.skynetsoftware.avnlauncher.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.Database
import org.skynetsoftware.avnlauncher.data.mapper.toGame
import org.skynetsoftware.avnlauncher.data.mapper.toGameEntity
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlayState
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository

internal fun Module.gamesRepositoryKoinModule(coroutineDispatcher: CoroutineDispatcher) {
    single<GamesRepository> {
        GamesRepositoryImpl(get(), coroutineDispatcher)
    }
}

@Suppress("TooManyFunctions")
private class GamesRepositoryImpl(
    private val database: Database,
    private val coroutineDispatcher: CoroutineDispatcher,
) : GamesRepository {
    override val games: Flow<List<Game>> =
        database.gameEntityQueries.all().asFlow().mapToList(coroutineDispatcher).map {
            it.map { it.toGame() }
        }

    override suspend fun all(): List<Game> {
        return withContext(coroutineDispatcher) { database.gameEntityQueries.all().executeAsList().map { it.toGame() } }
    }

    override suspend fun get(id: Int): Game? {
        return withContext(coroutineDispatcher) { database.gameEntityQueries.get(id).executeAsOneOrNull()?.toGame() }
    }

    override suspend fun updateRating(
        id: Int,
        rating: Int,
    ) {
        withContext(coroutineDispatcher) { database.gameEntityQueries.updateRating(rating, id) }
    }

    override suspend fun updateFavorite(
        id: Int,
        favorite: Boolean,
    ) {
        withContext(coroutineDispatcher) { database.gameEntityQueries.updateFavorite(favorite, id) }
    }

    override suspend fun updateExecutablePaths(games: List<Pair<Int, Set<String>>>) {
        withContext(coroutineDispatcher) {
            games.forEach {
                database.gameEntityQueries.updateExecutablePaths(it.second, it.first)
            }
        }
    }

    override suspend fun insertGame(game: Game) {
        withContext(coroutineDispatcher) { database.gameEntityQueries.insert(game.toGameEntity()) }
    }

    override suspend fun updateGames(games: List<Game>) {
        withContext(coroutineDispatcher) {
            games.forEach { game ->
                database.gameEntityQueries.updateGame(
                    title = game.title,
                    executablePaths = game.executablePaths,
                    imageUrl = game.imageUrl,
                    customImageUrl = game.customImageUrl,
                    checkForUpdates = game.checkForUpdates,
                    version = game.version,
                    playTime = game.playTime,
                    rating = game.rating,
                    f95Rating = game.f95Rating,
                    updateAvailable = game.updateAvailable,
                    added = game.added,
                    lastPlayed = game.lastPlayed,
                    lastUpdateCheck = game.lastUpdateCheck,
                    hidden = game.hidden,
                    releaseDate = game.releaseDate,
                    firstReleaseDate = game.firstReleaseDate,
                    playState = game.playState,
                    availableVersion = game.availableVersion,
                    tags = game.tags,
                    lastRedirectUrl = game.lastRedirectUrl,
                    firstPlayed = game.firstPlayed,
                    f95ZoneThreadId = game.f95ZoneThreadId,
                    notes = game.notes,
                    favorite = game.favorite,
                )
            }
        }
    }

    override suspend fun updateGame(
        id: Int,
        title: String,
        executablePaths: Set<String>,
        customImageUrl: String,
        checkForUpdates: Boolean,
        playState: PlayState,
        hidden: Boolean,
        notes: String?,
    ) {
        withContext(coroutineDispatcher) {
            database.gameEntityQueries.updateGame2(
                title,
                executablePaths,
                customImageUrl,
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
            database.gameEntityQueries.updateVersion(updateAvailable, version, availableVersion, id)
        }
    }

    override suspend fun updateGame(
        id: Int,
        playTime: Long,
        lastPlayed: Long,
    ) {
        withContext(coroutineDispatcher) {
            database.gameEntityQueries.updatePlayTime(playTime, lastPlayed, id)
        }
    }
}
