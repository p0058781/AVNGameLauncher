package org.skynetsoftware.avnlauncher.domain.repository

import kotlinx.coroutines.flow.Flow
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlayState

interface GamesRepository {
    val games: Flow<List<Game>>

    // read
    suspend fun all(): List<Game>

    // write

    suspend fun updateRating(
        id: Int,
        rating: Int,
    )

    suspend fun updateHidden(
        id: Int,
        hidden: Boolean,
    )

    suspend fun updatePlayState(
        id: Int,
        playState: PlayState,
    )

    suspend fun updateExecutablePaths(games: List<Pair<Int, Set<String>>>)

    suspend fun insertGame(game: Game)

    suspend fun updateGames(games: List<Game>)

    suspend fun updateGame(
        id: Int,
        title: String,
        executablePaths: Set<String>,
        imageUrl: String,
        checkForUpdates: Boolean,
    )

    suspend fun updateGame(
        id: Int,
        updateAvailable: Boolean,
        version: String,
        availableVersion: String?,
    )

    suspend fun updateGame(
        id: Int,
        playTime: Long,
        lastPlayed: Long,
    )
}
