package org.skynetsoftware.avnlauncher.ui.screen.customlists

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.playStateErrorAlreadyExists
import org.skynetsoftware.avnlauncher.domain.coroutines.CoroutineDispatchers
import org.skynetsoftware.avnlauncher.domain.model.GamesList
import org.skynetsoftware.avnlauncher.domain.repository.GameListsRepository

class CustomListsViewModel(
    private val gameListsRepository: GameListsRepository,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val unsavedGamesLists = MutableStateFlow(emptyList<GamesListViewItem>())

    private val savedGamesLists = gameListsRepository.gamesLists

    val gamesLists =
        combine(savedGamesLists, unsavedGamesLists) { savedGamesLists, unsavedGamesLists ->
            ArrayList<GamesListViewItem>().apply {
                addAll(
                    savedGamesLists.map { savedGamesList ->
                        unsavedGamesLists.find { unsavedGamesLists -> unsavedGamesLists.id == savedGamesList.id }
                            ?: GamesListViewItem(
                                id = savedGamesList.id,
                                name = savedGamesList.name,
                                description = savedGamesList.description,
                                editing = false,
                                error = null,
                            )
                    },
                )
                addAll(unsavedGamesLists.filter { it.id == 0 })
            }
        }

    fun edit(gamesListViewItem: GamesListViewItem) =
        viewModelScope.launch {
            if (gamesListViewItem.id == 0) {
                return@launch
            }
            val unsavedPlayStates = unsavedGamesLists.value.toMutableList()
            unsavedPlayStates.removeAll { it.id == gamesListViewItem.id }
            unsavedPlayStates.add(gamesListViewItem.copy(editing = true))
            this@CustomListsViewModel.unsavedGamesLists.emit(unsavedPlayStates)
        }

    fun delete(gamesListViewItem: GamesListViewItem) =
        viewModelScope.launch(coroutineDispatchers.io) {
            if (gamesListViewItem.id == 0) {
                return@launch
            }
            gameListsRepository.delete(gamesListViewItem.id)
        }

    fun save(
        gamesListViewItem: GamesListViewItem,
        name: String,
        description: String?,
    ) = viewModelScope.launch(coroutineDispatchers.io) {
        if (gamesListViewItem.id == 0) {
            val existingPlayState = gameListsRepository.getByName(name)
            if (existingPlayState != null) {
                setError(gamesListViewItem, Res.string.playStateErrorAlreadyExists)
            } else {
                gameListsRepository.insert(
                    GamesList(
                        id = 0,
                        name = name,
                        description = description,
                    ),
                )
                val unsavedPlayStates = unsavedGamesLists.value.toMutableList()
                unsavedPlayStates.removeIf { it == gamesListViewItem }
                this@CustomListsViewModel.unsavedGamesLists.emit(unsavedPlayStates)
            }
        } else {
            gameListsRepository.update(
                gamesListViewItem.id,
                name = name,
                description = description,
            )
            val unsavedPlayStates = unsavedGamesLists.value.toMutableList()
            unsavedPlayStates.removeIf { it == gamesListViewItem }
            this@CustomListsViewModel.unsavedGamesLists.emit(unsavedPlayStates)
        }
    }

    fun add() =
        viewModelScope.launch {
            val unsavedPlayStates = unsavedGamesLists.value.toMutableList()
            unsavedPlayStates.add(
                GamesListViewItem(
                    id = 0,
                    name = "",
                    description = null,
                    editing = true,
                    error = null,
                ),
            )
            this@CustomListsViewModel.unsavedGamesLists.emit(unsavedPlayStates)
        }

    private fun setError(
        gamesListViewItem: GamesListViewItem,
        error: StringResource?,
    ) = viewModelScope.launch {
        val unsavedPlayStates = unsavedGamesLists.value.toMutableList()
        val index = unsavedPlayStates.indexOfFirst { it == gamesListViewItem }
        if (index >= 0) {
            val newPlayState = gamesListViewItem.copy(error = error)
            unsavedPlayStates[index] = newPlayState
        }
        this@CustomListsViewModel.unsavedGamesLists.emit(unsavedPlayStates)
    }
}
