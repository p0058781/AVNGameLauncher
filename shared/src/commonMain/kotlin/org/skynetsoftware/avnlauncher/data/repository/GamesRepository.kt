package org.skynetsoftware.avnlauncher.data.repository

import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.database.model.RealmGame
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.model.PlayState
import org.skynetsoftware.avnlauncher.data.model.toGame

val gamesRepositoryKoinModule = module {
    single<GamesRepository> { GamesRepositoryImpl(get()) }
}

interface GamesRepository {
    val games: Flow<List<Game>>
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

    suspend fun updateReleaseDate(releaseDate: String?, game: Game)

    suspend fun updateAvailableVersion(availableVersion: String?, game: Game)

    suspend fun updateVersion(version: String, game: Game)

    suspend fun updateRating(rating: Int, game: Game)

    suspend fun updateHidden(hidden: Boolean, game: Game)

    suspend fun updatePlayState(playState: PlayState, game: Game)

    suspend fun updateExecutablePath(executablePath: String, game: Game)

    suspend fun updateTitle(title: String, game: Game)

    suspend fun updateImageUrl(imageUrl: String, game: Game)

    suspend fun insertGame(
        title: String,
        imageUrl: String,
        f95ZoneUrl: String,
        executablePath: String,
        version: String
    )

    //repo
    fun setSortOrder(sortOrder: SortOrder)

    fun setSortDirection(sortDirection: SortDirection)

    fun setFilter(filter: Filter)
}

private class GamesRepositoryImpl(
    private val realm: Realm
) : GamesRepository {

    //sorting
    override val sortOrder = MutableStateFlow<SortOrder>(SortOrder.LastPlayed)
    override val sortDirection = MutableStateFlow(SortDirection.Descending)

    //filtering
    override val filter = MutableStateFlow<Filter>(Filter.Playing)

    //data
    override val games = combine(
        realm.query<RealmGame>().find().asFlow(),
        sortOrder,
        sortDirection,
        filter
    ) { results, sortOrder, sortDirection, filter ->
        filterAndSortResults(results, sortOrder, sortDirection, filter)
    }

    private fun filterAndSortResults(
        results: ResultsChange<RealmGame>,
        sortOrder: SortOrder,
        sortDirection: SortDirection,
        filter: Filter
    ): List<Game> {
        return sortOrder.sort(filter.filter(results.list.map { it.toGame() }), sortDirection)
    }

    private fun findRealmGame(game: Game) = realm.query<RealmGame>("title == $0", game.title).first().find()

    override suspend fun all(): List<Game> {
        return realm.query<RealmGame>().find().map(RealmGame::toGame)
    }

    override suspend fun updatePlayTime(playTime: Long, game: Game) = realm.write {
        findRealmGame(game)?.playTime = playTime
    }

    override suspend fun updateLastPlayed(lastPlayed: Long, game: Game) = realm.write {
        findRealmGame(game)?.lastPlayed = lastPlayed
    }

    override suspend fun updateLastUpdateCheck(lastUpdateCheck: Long, game: Game) = realm.write {
        findRealmGame(game)?.lastUpdateCheck = lastUpdateCheck
    }

    override suspend fun updateUpdateAvailable(updateAvailable: Boolean, game: Game) = realm.write {
        findRealmGame(game)?.updateAvailable = updateAvailable
    }

    override suspend fun updateAvailableVersion(availableVersion: String?, game: Game) = realm.write {
        findRealmGame(game)?.availableVersion = availableVersion
    }

    override suspend fun updateVersion(version: String, game: Game) = realm.write {
        findRealmGame(game)?.version = version
    }

    override suspend fun updateRating(rating: Int, game: Game) = realm.write {
        findRealmGame(game)?.rating = rating
    }

    override suspend fun updateHidden(hidden: Boolean, game: Game) = realm.write {
        findRealmGame(game)?.hidden = hidden
    }

    override suspend fun updatePlayState(playState: PlayState, game: Game) = realm.write {
        findRealmGame(game)?.playState = playState.name
    }

    override suspend fun updateExecutablePath(executablePath: String, game: Game) = realm.write {
        findRealmGame(game)?.executablePath = executablePath
    }

    override suspend fun updateTitle(title: String, game: Game) = realm.write {
        findRealmGame(game)?.title = title
    }

    override suspend fun updateImageUrl(imageUrl: String, game: Game) = realm.write {
        findRealmGame(game)?.imageUrl = imageUrl
    }

    override suspend fun updateReleaseDate(releaseDate: String?, game: Game) = realm.write {
        findRealmGame(game)?.releaseDate = releaseDate
    }

    override suspend fun insertGame(
        title: String,
        imageUrl: String,
        f95ZoneUrl: String,
        executablePath: String,
        version: String
    ) = realm.write {
        val game = RealmGame().apply {
            this.title = title
            this.imageUrl = imageUrl
            this.f95ZoneUrl = f95ZoneUrl
            this.executablePath = executablePath
            this.version = version
        }
        copyToRealm(game)
        Unit
    }

    override fun setSortOrder(sortOrder: SortOrder) {
        this.sortOrder.value = sortOrder
    }

    override fun setSortDirection(sortDirection: SortDirection) {
        this.sortDirection.value = sortDirection
    }

    override fun setFilter(filter: Filter) {
        this.filter.value = filter
    }
}