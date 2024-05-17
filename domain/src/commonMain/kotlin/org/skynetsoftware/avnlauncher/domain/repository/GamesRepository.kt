package org.skynetsoftware.avnlauncher.domain.repository

import kotlinx.coroutines.flow.Flow
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlayState

interface GamesRepository {
    val games: Flow<List<Game>>

    // read
    suspend fun all(): List<Game>

    suspend fun get(id: Int): Game?

    // write

    suspend fun updateRating(
        id: Int,
        rating: Int,
    )

    suspend fun updateFavorite(
        id: Int,
        favorite: Boolean,
    )

    suspend fun updateExecutablePaths(games: List<Pair<Int, Set<String>>>)

    suspend fun insertGame(game: Game)

    suspend fun updateGames(games: List<Game>)

    @Suppress("LongParameterList")
    suspend fun updateGame(
        id: Int,
        executablePaths: Set<String>,
        checkForUpdates: Boolean,
        playState: PlayState,
        hidden: Boolean,
        notes: String?,
    )

    @Suppress("LongParameterList")
    suspend fun updateGame(
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
    )

    suspend fun updateGame(
        id: Int,
        updateAvailable: Boolean,
        version: String,
        availableVersion: String?,
    )
}
