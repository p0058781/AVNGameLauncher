package org.skynetsoftware.avnlauncher.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.model.Filter
import org.skynetsoftware.avnlauncher.data.model.SortDirection
import org.skynetsoftware.avnlauncher.data.model.SortOrder
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.model.Games
import java.io.File

val gamesRepositoryKoinModule = module {
    single<GamesRepository> { GamesRepositoryImpl(get()) }
}

interface GamesRepository {
    val games: StateFlow<List<Game>>
    val sortOrder: StateFlow<SortOrder>
    val sortDirection: StateFlow<SortDirection>
    val filter: StateFlow<Filter>

    fun selectAll(): List<Game>

    fun updatePlayTime(playTime: Long, id: EntityID<Int>)

    fun updateLastPlayed(lastPlayed: Long, id: EntityID<Int>)

    fun updateLastUpdateCheck(lastUpdateCheck: Long, id: EntityID<Int>)

    fun updateUpdateAvailable(updateAvailable: Boolean, id: EntityID<Int>)

    fun updateReleaseDate(releaseDate: String?, id: EntityID<Int>)

    fun updateAvailableVersion(availableVersion: String?, id: EntityID<Int>)

    fun updateVersion(version: String, id: EntityID<Int>)

    fun updateRating(rating: Int, id: EntityID<Int>)

    fun updateHidden(hidden: Boolean, id: EntityID<Int>)

    fun setPlaying(playing: Boolean, id: EntityID<Int>)

    fun setCompleted(completed: Boolean, id: EntityID<Int>)

    fun setWaitingForUpdate(waiting: Boolean, id: EntityID<Int>)

    fun setSortOrder(sortOrder: SortOrder)

    fun setSortDirection(sortDirection: SortDirection)

    fun setFilter(filter: Filter)

    fun updateExecutablePath(executablePath: String, id: EntityID<Int>)

    fun updateTitle(title: String, id: EntityID<Int>)

    fun updateImageUrl(imageUrl: String, id: EntityID<Int>)

    fun insertGame(title: String, imageUrl: String, f95ZoneUrl: String, executablePath: String, version: String, added: Long)
}

private class GamesRepositoryImpl(
    private val database: Database
) : GamesRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    //data
    override val games = MutableStateFlow<List<Game>>(emptyList())

    //sorting
    override val sortOrder = MutableStateFlow(SortOrder.LastPlayed)
    override val sortDirection = MutableStateFlow(SortDirection.Descending)

    //filtering
    override val filter = MutableStateFlow(Filter.Playing)

    init {
        loadGames()
        //check paths
        repositoryScope.launch {
            selectAll().forEach {
                if (!File(it.executablePath).exists()) {
                    updateExecutablePath("", it.id)
                }
            }
        }
    }

    private fun loadGames() {
        repositoryScope.launch {
            games.value = transaction {
                val query = Games.selectAll()
                var showHiddenGames = false
                when (filter.value) {
                    Filter.GamesWithUpdate -> {
                        query.andWhere {
                            Games.updateAvailable eq true
                        }
                    }

                    Filter.HiddenGames -> {
                        showHiddenGames = true
                    }

                    Filter.UnplayedGames -> {
                        query.andWhere {
                            Games.playTime eq null
                        }
                        query.orWhere {
                            Games.playTime less 3600_000L
                        }
                    }

                    Filter.Playing -> {
                        query.andWhere {
                            Games.playing eq true
                        }
                    }

                    Filter.Completed -> {
                        query.andWhere {
                            Games.completed eq true
                        }
                    }

                    Filter.WaitingForUpdate -> {
                        query.andWhere {
                            Games.waitingForUpdate eq true
                        }
                    }

                    Filter.All -> {

                    }
                }
                query.andWhere {
                    Games.hidden eq showHiddenGames
                }

                query.orderBy(sortOrder.value.column, sortDirection.value.sortOrder).map { Game.wrapRow(it) }
            }
        }
    }

    override fun selectAll(): List<Game> {
        return transaction { Game.all().toList() }
    }

    override fun updatePlayTime(playTime: Long, id: EntityID<Int>) {
        transaction {
            Games.update({ Games.id eq id }) {
                it[Games.playTime] = playTime
            }
        }
        loadGames()
    }

    override fun updateLastPlayed(lastPlayed: Long, id: EntityID<Int>) {
        transaction {
            Games.update({ Games.id eq id }) {
                it[Games.lastPlayed] = lastPlayed
            }
        }
        loadGames()
    }

    override fun updateLastUpdateCheck(lastUpdateCheck: Long, id: EntityID<Int>) {
        transaction {
            Games.update({ Games.id eq id }) {
                it[Games.lastUpdateCheck] = lastUpdateCheck
            }
        }
        loadGames()
    }

    override fun updateUpdateAvailable(updateAvailable: Boolean, id: EntityID<Int>) {
        transaction {
            Games.update({ Games.id eq id }) {
                it[Games.updateAvailable] = updateAvailable
            }
        }
        loadGames()
    }

    override fun updateAvailableVersion(availableVersion: String?, id: EntityID<Int>) {
        transaction {
            Games.update({ Games.id eq id }) {
                it[Games.availableVersion] = availableVersion
            }
        }
        loadGames()
    }

    override fun updateVersion(version: String, id: EntityID<Int>) {
        transaction {
            Games.update({ Games.id eq id }) {
                it[Games.version] = version
            }
        }
        loadGames()
    }

    override fun updateRating(rating: Int, id: EntityID<Int>) {
        transaction {
            Games.update({ Games.id eq id }) {
                it[Games.rating] = rating
            }
        }
        loadGames()
    }

    override fun updateHidden(hidden: Boolean, id: EntityID<Int>) {
        transaction {
            Games.update({ Games.id eq id }) {
                it[Games.hidden] = hidden
            }
        }
        loadGames()
    }

    override fun setPlaying(playing: Boolean, id: EntityID<Int>) {
        transaction {
            Games.update({ Games.id eq id }) {
                it[Games.playing] = playing
                it[Games.completed] = false
                it[Games.waitingForUpdate] = false
            }
        }
        loadGames()
    }

    override fun setCompleted(completed: Boolean, id: EntityID<Int>) {
        transaction {
            Games.update({ Games.id eq id }) {
                it[Games.completed] = completed
                it[Games.playing] = false
                it[Games.waitingForUpdate] = false
            }
        }
        loadGames()
    }

    override fun setWaitingForUpdate(waiting: Boolean, id: EntityID<Int>) {
        transaction {
            Games.update({ Games.id eq id }) {
                it[Games.waitingForUpdate] = waiting
                it[Games.completed] = false
                it[Games.playing] = false
            }
        }
        loadGames()
    }

    override fun setSortOrder(sortOrder: SortOrder) {
        this.sortOrder.value = sortOrder
        loadGames()
    }

    override fun setSortDirection(sortDirection: SortDirection) {
        this.sortDirection.value = sortDirection
        loadGames()
    }

    override fun setFilter(filter: Filter) {
        this.filter.value = filter
        loadGames()
    }

    override fun updateExecutablePath(executablePath: String, id: EntityID<Int>) {
        transaction {
            Games.update({ Games.id eq id }) {
                it[Games.executablePath] = executablePath
            }
        }
        loadGames()
    }

    override fun updateTitle(title: String, id: EntityID<Int>) {
        transaction {
            Games.update({ Games.id eq id }) {
                it[Games.title] = title
            }
        }
        loadGames()
    }

    override fun updateImageUrl(imageUrl: String, id: EntityID<Int>) {
        transaction {
            Games.update({ Games.id eq id }) {
                it[Games.imageUrl] = imageUrl
            }
        }
        loadGames()
    }

    override fun updateReleaseDate(releaseDate: String?, id: EntityID<Int>) {
        transaction {
            Games.update({ Games.id eq id }) {
                it[Games.releaseDate] = releaseDate
            }
        }
        loadGames()
    }

    override fun insertGame(title: String, imageUrl: String, f95ZoneUrl: String, executablePath: String, version: String, added: Long) {
        transaction {
            Games.insert {
                it[Games.title] = title
                it[Games.imageUrl] = imageUrl
                it[Games.f95ZoneUrl] = f95ZoneUrl
                it[Games.executablePath] = executablePath
                it[Games.version] = version
                it[Games.added] = System.currentTimeMillis()
            }
        }
        loadGames()
    }
}