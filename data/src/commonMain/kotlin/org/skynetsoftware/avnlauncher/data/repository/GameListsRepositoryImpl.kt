package org.skynetsoftware.avnlauncher.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.Database
import org.skynetsoftware.avnlauncher.data.GameEntityToListEntity
import org.skynetsoftware.avnlauncher.data.ListEntityQueries
import org.skynetsoftware.avnlauncher.data.mapper.toGamesList
import org.skynetsoftware.avnlauncher.data.mapper.toListEntity
import org.skynetsoftware.avnlauncher.domain.coroutines.CoroutineDispatchers
import org.skynetsoftware.avnlauncher.domain.model.GamesList
import org.skynetsoftware.avnlauncher.domain.repository.GameListsRepository

internal fun Module.gameListsRepositoryKoinModule() {
    single<GameListsRepository> {
        val database = get<Database>()
        GameListsRepositoryImpl(database.listEntityQueries, get())
    }
}

private class GameListsRepositoryImpl(
    private val listEntityQueries: ListEntityQueries,
    private val coroutineDispatchers: CoroutineDispatchers,
) : GameListsRepository {
    override val gamesLists: Flow<List<GamesList>> =
        listEntityQueries.gamesLists().asFlow().mapToList(coroutineDispatchers.io).map { it.map { it.toGamesList() } }

    override fun getById(id: Int): GamesList? {
        return listEntityQueries.getById(id).executeAsOneOrNull()?.toGamesList()
    }

    override fun getByName(name: String): GamesList? {
        return listEntityQueries.getByName(name).executeAsOneOrNull()?.toGamesList()
    }

    override fun insert(gamesList: GamesList) {
        listEntityQueries.insert(gamesList.toListEntity())
    }

    override fun insertGameToGameList(
        gameId: Int,
        listId: Int,
    ) {
        listEntityQueries.insertGameToGamesList(GameEntityToListEntity(gameId, listId))
    }

    override fun deleteGameToGamesList(
        gameId: Int,
        listId: Int,
    ) {
        listEntityQueries.deleteGameToGamesList(gameId, listId)
    }

    override fun update(
        id: Int,
        name: String,
        description: String?,
    ) {
        listEntityQueries.update(name, description, id)
    }

    override fun delete(id: Int) {
        listEntityQueries.delete(id)
    }
}
