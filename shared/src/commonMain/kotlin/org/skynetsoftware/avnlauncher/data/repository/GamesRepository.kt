package org.skynetsoftware.avnlauncher.data.repository

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.database.model.RealmGame
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.model.PlayState
import org.skynetsoftware.avnlauncher.data.model.toGame
import org.skynetsoftware.avnlauncher.data.model.toRealmGame
import org.skynetsoftware.avnlauncher.settings.SettingsManager
import org.skynetsoftware.avnlauncher.sync.SyncApi
import org.skynetsoftware.avnlauncher.sync.SyncGame
import org.skynetsoftware.avnlauncher.sync.SyncService

val gamesRepositoryKoinModule = module {
    single<GamesRepository> { GamesRepositoryImpl(get(), get(), get()) }
}

interface GamesRepository {
    val games: Flow<List<Game>>
    val totalPlayTime: Flow<Long>
    val firstPlayedTime: Flow<Long>
    val sortOrder: StateFlow<SortOrder>
    val sortDirection: StateFlow<SortDirection>
    val filter: StateFlow<Filter>

    //read
    suspend fun all(): List<Game>

    //write
    suspend fun updatePlayTime(playTime: Long, game: Game)

    suspend fun updateLastPlayed(lastPlayed: Long, game: Game)

    suspend fun updateLastUpdateCheck(lastUpdateCheck: Long, game: Game)

    suspend fun updateUpdateAvailable(updateAvailable: Boolean, game: Game)

    suspend fun updateReleaseDate(releaseDate: Long, game: Game)

    suspend fun updateFirstReleaseDate(firstReleaseDate: Long, game: Game)

    suspend fun updateAvailableVersion(availableVersion: String?, game: Game)

    suspend fun updateVersion(version: String, game: Game)

    suspend fun updateRating(rating: Int, game: Game)

    suspend fun updateHidden(hidden: Boolean, game: Game)

    suspend fun updatePlayState(playState: PlayState, game: Game)

    suspend fun updateExecutablePath(executablePath: String, game: Game)

    suspend fun updateTitle(title: String, game: Game)

    suspend fun updateImageUrl(imageUrl: String, game: Game)

    suspend fun updateLastRedirectUrl(lastRedirectUrl: String, game: Game)

    suspend fun insertGame(game: Game)

    //repo
    fun setSortOrder(sortOrder: SortOrder)

    fun setSortDirection(sortDirection: SortDirection)

    fun setFilter(filter: Filter)
}

private class GamesRepositoryImpl(
    private val realm: Realm,
    private val settingsManager: SettingsManager,
    private val syncApi: SyncApi
) : GamesRepository {

    private val remoteClientMode = settingsManager.remoteClientMode
    private val refreshFromRemote = MutableSharedFlow<Unit>(replay = 1)

    //TODO loading indicator

    private val remoteGames = combine(remoteClientMode, refreshFromRemote) { remoteClientMode, refreshFromRemote ->
        //TODO test refresh
        if(remoteClientMode) {
            syncApi.get()
        } else {
            emptyList()
        }
    }

    private val realmGames = realm.query<RealmGame>().find().asFlow()

    //sorting
    override val sortOrder = settingsManager.selectedSortOrder
    override val sortDirection = settingsManager.selectedSortDirection

    //filtering
    override val filter = settingsManager.selectedFilter

    //data
    override val games = combine(
        remoteClientMode,
        remoteGames,
        realmGames,
        sortOrder,
        sortDirection,
        filter
    ) { values ->
        val remoteClientMode = values[0] as Boolean
        val remoteGames = values[1] as List<SyncGame>
        val realmGames = values[2] as ResultsChange<RealmGame>
        val sortOrder = values[3] as SortOrder
        val sortDirection = values[4] as SortDirection
        val filter = values[5] as Filter

        val games = if(remoteClientMode) remoteGames.map { it.toGame() } else realmGames.list.map { it.toGame() }

        filterAndSortResults(games, sortOrder, sortDirection, filter)
    }

    override val totalPlayTime: Flow<Long> = combine(remoteClientMode, realmGames, remoteGames) { remoteClientMode, realmGames, remoteGames ->
        val games = if(remoteClientMode) remoteGames.map { it.toGame() } else realmGames.list.map { it.toGame() }
        games.sumOf { it.playTime }
    }

    override val firstPlayedTime: Flow<Long> = combine(remoteClientMode, realmGames, remoteGames) { remoteClientMode, realmGames, remoteGames ->
        val games = if(remoteClientMode) remoteGames.map { it.toGame() } else realmGames.list.map { it.toGame() }
        games.minOf { it.added }
    }

    init {
        refreshFromRemote.tryEmit(Unit)
    }

    private fun filterAndSortResults(
        games: List<Game>,
        sortOrder: SortOrder,
        sortDirection: SortDirection,
        filter: Filter
    ): List<Game> {
        return sortOrder.sort(filter.filter(games), sortDirection)
    }

    private fun MutableRealm.findRealmGame(game: Game) = query<RealmGame>("title == $0", game.title).first().find()

    private suspend fun <R> realmWrite(block: MutableRealm.() -> R): R {
        if(remoteClientMode.value)
            throw IllegalStateException("remote client mode is read only")
        return realm.write(block)
    }

    override suspend fun all(): List<Game> {
        return if(remoteClientMode.value) syncApi.get().map { it.toGame() } else realm.query<RealmGame>().find().map(RealmGame::toGame)
    }

    override suspend fun updatePlayTime(playTime: Long, game: Game) = realmWrite {
        findRealmGame(game)?.playTime = playTime
    }

    override suspend fun updateLastPlayed(lastPlayed: Long, game: Game) = realmWrite {
        findRealmGame(game)?.lastPlayed = lastPlayed
    }

    override suspend fun updateLastUpdateCheck(lastUpdateCheck: Long, game: Game) = realmWrite {
        findRealmGame(game)?.lastUpdateCheck = lastUpdateCheck
    }

    override suspend fun updateUpdateAvailable(updateAvailable: Boolean, game: Game) = realmWrite {
        findRealmGame(game)?.updateAvailable = updateAvailable
    }

    override suspend fun updateAvailableVersion(availableVersion: String?, game: Game) = realmWrite {
        findRealmGame(game)?.availableVersion = availableVersion
    }

    override suspend fun updateVersion(version: String, game: Game) = realmWrite {
        findRealmGame(game)?.version = version
    }

    override suspend fun updateRating(rating: Int, game: Game) = realmWrite {
        findRealmGame(game)?.rating = rating
    }

    override suspend fun updateHidden(hidden: Boolean, game: Game) = realmWrite {
        findRealmGame(game)?.hidden = hidden
    }

    override suspend fun updatePlayState(playState: PlayState, game: Game) = realmWrite {
        findRealmGame(game)?.playState = playState.name
    }

    override suspend fun updateExecutablePath(executablePath: String, game: Game) = realmWrite {
        findRealmGame(game)?.executablePath = executablePath
    }

    override suspend fun updateTitle(title: String, game: Game) = realmWrite {
        findRealmGame(game)?.title = title
    }

    override suspend fun updateImageUrl(imageUrl: String, game: Game) = realmWrite {
        findRealmGame(game)?.imageUrl = imageUrl
    }

    override suspend fun updateReleaseDate(releaseDate: Long, game: Game) = realmWrite {
        findRealmGame(game)?.releaseDate = releaseDate
    }

    override suspend fun updateFirstReleaseDate(firstReleaseDate: Long, game: Game) = realmWrite {
        findRealmGame(game)?.firstReleaseDate = firstReleaseDate
    }

    override suspend fun updateLastRedirectUrl(lastRedirectUrl: String, game: Game) = realmWrite  {
        findRealmGame(game)?.lastRedirectUrl = lastRedirectUrl
    }

    override suspend fun insertGame(
        game: Game
    ) = realmWrite {
        copyToRealm(game.toRealmGame().apply { added = Clock.System.now().toEpochMilliseconds() })
        Unit
    }

    override fun setSortOrder(sortOrder: SortOrder) {
        settingsManager.setSelectedSortOrder(sortOrder)
    }

    override fun setSortDirection(sortDirection: SortDirection) {
        settingsManager.setSelectedSortDirection(sortDirection)
    }

    override fun setFilter(filter: Filter) {
        settingsManager.setSelectedFilter(filter)
    }
}