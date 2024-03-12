package org.skynetsoftware.avnlauncher.data.repository

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmSetOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.database.model.RealmGame
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.model.PlayState
import org.skynetsoftware.avnlauncher.data.model.toGame
import org.skynetsoftware.avnlauncher.data.model.toRealmGame
import org.skynetsoftware.avnlauncher.sync.SyncApi

val gamesRepositoryKoinModule = module {
    single<GamesRepository> {
        val configManager = get<ConfigManager>()
        if (configManager.remoteClientMode) {
            GamesRepositorySyncApi(get())
        } else {
            GamesRepositoryRealm(get())
        }
    }
}

interface GamesRepository {
    val games: Flow<List<Game>>

    // read
    suspend fun all(): List<Game>

    suspend fun refresh()

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

private class GamesRepositoryRealm(
    private val realm: Realm,
) : GamesRepository {
    override val games: Flow<List<Game>> = realm.query<RealmGame>().find().asFlow().map { resultChange ->
        resultChange.list.map { it.toGame() }
    }

    override suspend fun all(): List<Game> {
        return realm.query<RealmGame>().find().map(RealmGame::toGame)
    }

    override suspend fun refresh() {
        // nothing, realm flow is always up-to-date
    }

    override suspend fun updatePlayTime(
        id: Int,
        playTime: Long,
    ) = realmWrite {
        findRealmGame(id)?.playTime = playTime
    }

    override suspend fun updateLastPlayed(
        id: Int,
        lastPlayed: Long,
    ) = realmWrite {
        findRealmGame(id)?.lastPlayed = lastPlayed
    }

    override suspend fun updateLastUpdateCheck(
        id: Int,
        lastUpdateCheck: Long,
    ) = realmWrite {
        findRealmGame(id)?.lastUpdateCheck = lastUpdateCheck
    }

    override suspend fun updateUpdateAvailable(
        id: Int,
        updateAvailable: Boolean,
    ) = realmWrite {
        findRealmGame(id)?.updateAvailable = updateAvailable
    }

    override suspend fun updateAvailableVersion(
        id: Int,
        availableVersion: String?,
    ) = realmWrite {
        findRealmGame(id)?.availableVersion = availableVersion
    }

    override suspend fun updateVersion(
        id: Int,
        version: String,
    ) = realmWrite {
        findRealmGame(id)?.version = version
    }

    override suspend fun updateRating(
        id: Int,
        rating: Int,
    ) = realmWrite {
        findRealmGame(id)?.rating = rating
    }

    override suspend fun updateHidden(
        id: Int,
        hidden: Boolean,
    ) = realmWrite {
        findRealmGame(id)?.hidden = hidden
    }

    override suspend fun updatePlayState(
        id: Int,
        playState: PlayState,
    ) = realmWrite {
        findRealmGame(id)?.playState = playState.name
    }

    override suspend fun updateExecutablePaths(
        id: Int,
        executablePaths: Set<String>,
    ) = realmWrite {
        findRealmGame(id)?.executablePaths = realmSetOf(*executablePaths.toTypedArray())
    }

    override suspend fun updateExecutablePaths(games: List<Pair<Int, Set<String>>>) =
        realmWrite {
            games.forEach {
                findRealmGame(it.first)?.executablePaths = realmSetOf(*it.second.toTypedArray())
            }
        }

    override suspend fun updateTitle(
        id: Int,
        title: String,
    ) = realmWrite {
        findRealmGame(id)?.title = title
    }

    override suspend fun updateImageUrl(
        id: Int,
        imageUrl: String,
    ) = realmWrite {
        findRealmGame(id)?.imageUrl = imageUrl
    }

    override suspend fun updateReleaseDate(
        id: Int,
        releaseDate: Long,
    ) = realmWrite {
        findRealmGame(id)?.releaseDate = releaseDate
    }

    override suspend fun updateFirstReleaseDate(
        id: Int,
        firstReleaseDate: Long,
    ) = realmWrite {
        findRealmGame(id)?.firstReleaseDate = firstReleaseDate
    }

    override suspend fun updateLastRedirectUrl(
        id: Int,
        lastRedirectUrl: String,
    ) = realmWrite {
        findRealmGame(id)?.lastRedirectUrl = lastRedirectUrl
    }

    override suspend fun updateCheckForUpdates(
        id: Int,
        checkForUpdates: Boolean,
    ) = realmWrite {
        findRealmGame(id)?.checkForUpdates = checkForUpdates
    }

    override suspend fun insertGame(game: Game) =
        realmWrite {
            copyToRealm(game.toRealmGame().apply { added = Clock.System.now().toEpochMilliseconds() })
            Unit
        }

    override suspend fun updateGames(games: List<Game>) =
        realmWrite {
            games.forEach {
                copyToRealm(it.toRealmGame(), updatePolicy = UpdatePolicy.ALL)
            }
        }

    override suspend fun updateGame(
        id: Int,
        title: String,
        executablePaths: Set<String>,
        imageUrl: String,
        checkForUpdates: Boolean,
    ) = realmWrite {
        val realmGame = findRealmGame(id)
        realmGame?.checkForUpdates = checkForUpdates
        realmGame?.title = title
        realmGame?.imageUrl = imageUrl
        realmGame?.executablePaths = realmSetOf(*executablePaths.toTypedArray())
    }

    override suspend fun updateGame(
        id: Int,
        updateAvailable: Boolean,
        version: String,
        availableVersion: String?,
    ) = realmWrite {
        val realmGame = findRealmGame(id)
        realmGame?.updateAvailable = updateAvailable
        realmGame?.version = version
        realmGame?.availableVersion = availableVersion
    }

    override suspend fun updateGame(
        id: Int,
        playTime: Long,
        lastPlayed: Long,
    ) = realmWrite {
        val realmGame = findRealmGame(id)
        realmGame?.playTime = playTime
        realmGame?.lastPlayed = lastPlayed
    }

    private fun MutableRealm.findRealmGame(id: Int) = query<RealmGame>("f95ZoneThreadId == $0", id).first().find()

    private suspend fun <R> realmWrite(block: MutableRealm.() -> R): R {
        return realm.write(block)
    }
}

private class GamesRepositorySyncApi(
    private val syncApi: SyncApi,
) : GamesRepository {
    private val _games = MutableStateFlow<List<Game>>(emptyList())
    override val games: Flow<List<Game>> get() = _games

    override suspend fun all(): List<Game> {
        return syncApi.get().map { it.toGame() }
    }

    override suspend fun refresh() {
        _games.emit(all())
    }

    override suspend fun updatePlayTime(
        id: Int,
        playTime: Long,
    ) {
        notSupportedError()
    }

    override suspend fun updateLastPlayed(
        id: Int,
        lastPlayed: Long,
    ) {
        notSupportedError()
    }

    override suspend fun updateLastUpdateCheck(
        id: Int,
        lastUpdateCheck: Long,
    ) {
        notSupportedError()
    }

    override suspend fun updateUpdateAvailable(
        id: Int,
        updateAvailable: Boolean,
    ) {
        notSupportedError()
    }

    override suspend fun updateAvailableVersion(
        id: Int,
        availableVersion: String?,
    ) {
        notSupportedError()
    }

    override suspend fun updateVersion(
        id: Int,
        version: String,
    ) {
        notSupportedError()
    }

    override suspend fun updateRating(
        id: Int,
        rating: Int,
    ) {
        notSupportedError()
    }

    override suspend fun updateHidden(
        id: Int,
        hidden: Boolean,
    ) {
        notSupportedError()
    }

    override suspend fun updatePlayState(
        id: Int,
        playState: PlayState,
    ) {
        notSupportedError()
    }

    override suspend fun updateExecutablePaths(
        id: Int,
        executablePaths: Set<String>,
    ) {
        notSupportedError()
    }

    override suspend fun updateExecutablePaths(games: List<Pair<Int, Set<String>>>) {
        notSupportedError()
    }

    override suspend fun updateTitle(
        id: Int,
        title: String,
    ) {
        notSupportedError()
    }

    override suspend fun updateImageUrl(
        id: Int,
        imageUrl: String,
    ) {
        notSupportedError()
    }

    override suspend fun updateReleaseDate(
        id: Int,
        releaseDate: Long,
    ) {
        notSupportedError()
    }

    override suspend fun updateFirstReleaseDate(
        id: Int,
        firstReleaseDate: Long,
    ) {
        notSupportedError()
    }

    override suspend fun updateLastRedirectUrl(
        id: Int,
        lastRedirectUrl: String,
    ) {
        notSupportedError()
    }

    override suspend fun updateCheckForUpdates(
        id: Int,
        checkForUpdates: Boolean,
    ) {
        notSupportedError()
    }

    override suspend fun insertGame(game: Game) {
        notSupportedError()
    }

    override suspend fun updateGames(games: List<Game>) {
        notSupportedError()
    }

    override suspend fun updateGame(
        id: Int,
        title: String,
        executablePaths: Set<String>,
        imageUrl: String,
        checkForUpdates: Boolean,
    ) {
        notSupportedError()
    }

    override suspend fun updateGame(
        id: Int,
        playTime: Long,
        lastPlayed: Long,
    ) {
        notSupportedError()
    }

    override suspend fun updateGame(
        id: Int,
        updateAvailable: Boolean,
        version: String,
        availableVersion: String?,
    ) {
        notSupportedError()
    }

    private fun notSupportedError() {
        throw IllegalStateException("Operation not supported for ${this::class.simpleName}")
    }
}
