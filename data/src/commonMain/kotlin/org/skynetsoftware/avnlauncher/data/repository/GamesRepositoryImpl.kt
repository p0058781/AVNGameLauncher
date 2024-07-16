package org.skynetsoftware.avnlauncher.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.Database
import org.skynetsoftware.avnlauncher.data.GameEntityQueries
import org.skynetsoftware.avnlauncher.data.GameEntityToListEntity
import org.skynetsoftware.avnlauncher.data.ListEntityQueries
import org.skynetsoftware.avnlauncher.data.mapper.toGame
import org.skynetsoftware.avnlauncher.data.mapper.toGameEntity
import org.skynetsoftware.avnlauncher.data.mapper.toGames
import org.skynetsoftware.avnlauncher.domain.coroutines.CoroutineDispatchers
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.GamesList
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository

internal fun Module.gamesRepositoryKoinModule() {
    single<GamesRepository> {
        val database = get<Database>()
        GamesRepositoryImpl(database.gameEntityQueries, database.listEntityQueries, get())
    }
}

@Suppress("TooManyFunctions")
private class GamesRepositoryImpl(
    private val gameEntityQueries: GameEntityQueries,
    private val listEntityQueries: ListEntityQueries,
    private val coroutineDispatchers: CoroutineDispatchers,
) : GamesRepository {
    override val games: Flow<List<Game>> =
        gameEntityQueries.gamesFull().asFlow().mapToList(coroutineDispatchers.io).map { it.toGames() }

    override suspend fun all(): List<Game> {
        return withContext(coroutineDispatchers.io) {
            gameEntityQueries.gamesFull().executeAsList().toGames()
        }
    }

    override suspend fun get(id: Int): Game? {
        return withContext(coroutineDispatchers.io) {
            gameEntityQueries.gameFull(id).executeAsList().toGame()
        }
    }

    override fun getFlow(id: Int): Flow<Game?> {
        return gameEntityQueries.gameFull(id).asFlow().map { it.executeAsList().toGame() }
    }

    override suspend fun updateRating(
        id: Int,
        rating: Int,
    ) {
        withContext(coroutineDispatchers.io) { gameEntityQueries.updateRating(rating, id) }
    }

    override suspend fun updateExecutablePaths(games: List<Pair<Int, Set<String>>>) {
        withContext(coroutineDispatchers.io) {
            games.forEach {
                gameEntityQueries.updateExecutablePaths(it.second, it.first)
            }
        }
    }

    override suspend fun insertGame(game: Game) {
        withContext(coroutineDispatchers.io) { gameEntityQueries.insertGame(game.toGameEntity()) }
    }

    override suspend fun updateGames(games: List<Game>) {
        withContext(coroutineDispatchers.io) {
            games.forEach { game ->
                gameEntityQueries.updateGame(
                    title = game.title,
                    description = game.description,
                    developer = game.developer,
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
                    playState = game.playState.id,
                    availableVersion = game.availableVersion,
                    tags = game.tags,
                    f95ZoneThreadId = game.f95ZoneThreadId,
                    notes = game.notes,
                )
            }
        }
    }

    override suspend fun updateGame(
        id: Int,
        executablePaths: Set<String>,
        checkForUpdates: Boolean,
        playState: String,
        gamesLists: List<GamesList>,
        hidden: Boolean,
        notes: String?,
    ) {
        withContext(coroutineDispatchers.io) {
            gameEntityQueries.updateGameF95(
                executablePaths,
                checkForUpdates,
                playState,
                hidden,
                notes,
                id,
            )
            listEntityQueries.deleteAllGameToGamesList(id)
            gamesLists.forEach {
                listEntityQueries.insertGameToGamesList(GameEntityToListEntity(id, it.id))
            }
        }
    }

    override suspend fun updateGame(
        id: Int,
        title: String,
        description: String,
        developer: String,
        imageUrl: String,
        version: String,
        releaseDate: Long,
        firstReleaseDate: Long,
        tags: Set<String>,
        executablePaths: Set<String>,
        checkForUpdates: Boolean,
        playState: String,
        gamesLists: List<GamesList>,
        hidden: Boolean,
        notes: String?,
    ) {
        withContext(coroutineDispatchers.io) {
            gameEntityQueries.updateGameNonF95(
                title,
                description,
                developer,
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
            listEntityQueries.deleteAllGameToGamesList(id)
            gamesLists.forEach {
                listEntityQueries.insertGameToGamesList(GameEntityToListEntity(id, it.id))
            }
        }
    }

    override suspend fun updateGame(
        id: Int,
        updateAvailable: Boolean,
        version: String,
        availableVersion: String?,
    ) {
        withContext(coroutineDispatchers.io) {
            gameEntityQueries.updateVersion(updateAvailable, version, availableVersion, id)
        }
    }
}
