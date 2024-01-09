package org.skynetsoftware.avnlauncher.domain.repository

import kotlinx.coroutines.flow.Flow
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlayState

interface GamesRepository {
    val games: Flow<List<Game>>

    // read
    suspend fun all(): List<Game>

    // write
    suspend fun updatePlayTime(
        id: Int,
        playTime: Long,
    )

    suspend fun updateLastPlayed(
        id: Int,
        lastPlayed: Long,
    )

    suspend fun updateLastUpdateCheck(
        id: Int,
        lastUpdateCheck: Long,
    )

    suspend fun updateUpdateAvailable(
        id: Int,
        updateAvailable: Boolean,
    )

    suspend fun updateReleaseDate(
        id: Int,
        releaseDate: Long,
    )

    suspend fun updateFirstReleaseDate(
        id: Int,
        firstReleaseDate: Long,
    )

    suspend fun updateAvailableVersion(
        id: Int,
        availableVersion: String?,
    )

    suspend fun updateVersion(
        id: Int,
        version: String,
    )

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

    suspend fun updateExecutablePaths(
        id: Int,
        executablePaths: Set<String>,
    )

    suspend fun updateExecutablePaths(games: List<Pair<Int, Set<String>>>)

    suspend fun updateTitle(
        id: Int,
        title: String,
    )

    suspend fun updateImageUrl(
        id: Int,
        imageUrl: String,
    )

    suspend fun updateLastRedirectUrl(
        id: Int,
        lastRedirectUrl: String,
    )

    suspend fun updateCheckForUpdates(
        id: Int,
        checkForUpdates: Boolean,
    )

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
