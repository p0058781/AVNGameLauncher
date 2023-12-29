package org.skynetsoftware.avnlauncher.data.repository

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
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
        playTime: Long,
        game: Game,
    )

    suspend fun updateLastPlayed(
        lastPlayed: Long,
        game: Game,
    )

    suspend fun updateLastUpdateCheck(
        lastUpdateCheck: Long,
        game: Game,
    )

    suspend fun updateUpdateAvailable(
        updateAvailable: Boolean,
        game: Game,
    )

    suspend fun updateReleaseDate(
        releaseDate: Long,
        game: Game,
    )

    suspend fun updateFirstReleaseDate(
        firstReleaseDate: Long,
        game: Game,
    )

    suspend fun updateAvailableVersion(
        availableVersion: String?,
        game: Game,
    )

    suspend fun updateVersion(
        version: String,
        game: Game,
    )

    suspend fun updateRating(
        rating: Int,
        game: Game,
    )

    suspend fun updateHidden(
        hidden: Boolean,
        game: Game,
    )

    suspend fun updatePlayState(
        playState: PlayState,
        game: Game,
    )

    suspend fun updateExecutablePath(
        executablePath: String,
        game: Game,
    )

    suspend fun updateTitle(
        title: String,
        game: Game,
    )

    suspend fun updateImageUrl(
        imageUrl: String,
        game: Game,
    )

    suspend fun updateLastRedirectUrl(
        lastRedirectUrl: String,
        game: Game,
    )

    suspend fun updateCheckForUpdates(
        checkForUpdates: Boolean,
        game: Game,
    )

    suspend fun insertGame(game: Game)

    suspend fun updateGame(game: Game)
}

private class GamesRepositoryRealm(
    private val realm: Realm,
) : GamesRepository {
    override val games: Flow<List<Game>> = realm.query<RealmGame>().find().asFlow().map { it.list.map { it.toGame() } }

    override suspend fun all(): List<Game> {
        return realm.query<RealmGame>().find().map(RealmGame::toGame)
    }

    override suspend fun refresh() {
        // nothing, realm flow is always up-to-date
    }

    override suspend fun updatePlayTime(
        playTime: Long,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.playTime = playTime
    }

    override suspend fun updateLastPlayed(
        lastPlayed: Long,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.lastPlayed = lastPlayed
    }

    override suspend fun updateLastUpdateCheck(
        lastUpdateCheck: Long,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.lastUpdateCheck = lastUpdateCheck
    }

    override suspend fun updateUpdateAvailable(
        updateAvailable: Boolean,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.updateAvailable = updateAvailable
    }

    override suspend fun updateAvailableVersion(
        availableVersion: String?,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.availableVersion = availableVersion
    }

    override suspend fun updateVersion(
        version: String,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.version = version
    }

    override suspend fun updateRating(
        rating: Int,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.rating = rating
    }

    override suspend fun updateHidden(
        hidden: Boolean,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.hidden = hidden
    }

    override suspend fun updatePlayState(
        playState: PlayState,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.playState = playState.name
    }

    override suspend fun updateExecutablePath(
        executablePath: String,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.executablePath = executablePath
    }

    override suspend fun updateTitle(
        title: String,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.title = title
    }

    override suspend fun updateImageUrl(
        imageUrl: String,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.imageUrl = imageUrl
    }

    override suspend fun updateReleaseDate(
        releaseDate: Long,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.releaseDate = releaseDate
    }

    override suspend fun updateFirstReleaseDate(
        firstReleaseDate: Long,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.firstReleaseDate = firstReleaseDate
    }

    override suspend fun updateLastRedirectUrl(
        lastRedirectUrl: String,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.lastRedirectUrl = lastRedirectUrl
    }

    override suspend fun updateCheckForUpdates(
        checkForUpdates: Boolean,
        game: Game,
    ) = realmWrite {
        findRealmGame(game)?.checkForUpdates = checkForUpdates
    }

    override suspend fun insertGame(game: Game) =
        realmWrite {
            copyToRealm(game.toRealmGame().apply { added = Clock.System.now().toEpochMilliseconds() })
            Unit
        }

    override suspend fun updateGame(game: Game) =
        realmWrite {
            copyToRealm(game.toRealmGame(), updatePolicy = UpdatePolicy.ALL)
            Unit
        }

    private fun MutableRealm.findRealmGame(game: Game) = query<RealmGame>("title == $0", game.title).first().find()

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
        playTime: Long,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun updateLastPlayed(
        lastPlayed: Long,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun updateLastUpdateCheck(
        lastUpdateCheck: Long,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun updateUpdateAvailable(
        updateAvailable: Boolean,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun updateAvailableVersion(
        availableVersion: String?,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun updateVersion(
        version: String,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun updateRating(
        rating: Int,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun updateHidden(
        hidden: Boolean,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun updatePlayState(
        playState: PlayState,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun updateExecutablePath(
        executablePath: String,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun updateTitle(
        title: String,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun updateImageUrl(
        imageUrl: String,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun updateReleaseDate(
        releaseDate: Long,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun updateFirstReleaseDate(
        firstReleaseDate: Long,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun updateLastRedirectUrl(
        lastRedirectUrl: String,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun updateCheckForUpdates(
        checkForUpdates: Boolean,
        game: Game,
    ) {
        notSupportedError()
    }

    override suspend fun insertGame(game: Game) {
        notSupportedError()
    }

    override suspend fun updateGame(game: Game) {
        notSupportedError()
    }

    private fun notSupportedError() {
        throw IllegalStateException("Operation not supported for ${this::class.simpleName}")
    }
}
