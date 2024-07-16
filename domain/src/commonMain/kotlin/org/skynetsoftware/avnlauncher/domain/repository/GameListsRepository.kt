package org.skynetsoftware.avnlauncher.domain.repository

import kotlinx.coroutines.flow.Flow
import org.skynetsoftware.avnlauncher.domain.model.GamesList

interface GameListsRepository {
    val gamesLists: Flow<List<GamesList>>

    fun getById(id: Int): GamesList?

    fun getByName(name: String): GamesList?

    fun insert(gamesList: GamesList)

    fun insertGameToGameList(
        gameId: Int,
        listId: Int,
    )

    fun deleteGameToGamesList(
        gameId: Int,
        listId: Int,
    )

    fun update(
        id: Int,
        name: String,
        description: String?,
    )

    fun delete(id: Int)
}
