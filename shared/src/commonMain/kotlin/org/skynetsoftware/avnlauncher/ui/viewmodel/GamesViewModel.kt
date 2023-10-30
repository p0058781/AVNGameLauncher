package org.skynetsoftware.avnlauncher.ui.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.model.PlayState
import org.skynetsoftware.avnlauncher.data.repository.Filter
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.data.repository.SortDirection
import org.skynetsoftware.avnlauncher.data.repository.SortOrder

class GamesViewModel(private val gamesRepository: GamesRepository) : ViewModel() {

    val games = gamesRepository.games.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val filter = gamesRepository.filter
    val sortOrder = gamesRepository.sortOrder
    val sortDirection = gamesRepository.sortDirection

    init {
        //check paths
        /*repositoryScope.launch {
            selectAll().forEach {
                if (!File(it.executablePath).exists()) {
                    updateExecutablePath("", it.id)
                }
            }
        }*/
        //TODO
    }

    fun setFilter(filter: Filter) {
        gamesRepository.setFilter(filter)
    }

    fun setSortOrder(sortOrder: SortOrder) {
        gamesRepository.setSortOrder(sortOrder)
    }

    fun setSortDirection(sortDirection: SortDirection) {
        gamesRepository.setSortDirection(sortDirection)
    }

    fun resetUpdateAvailable(availableVersion: String, game: Game) = viewModelScope.launch {
        gamesRepository.updateUpdateAvailable(false, game)
        gamesRepository.updateVersion(availableVersion, game)
        gamesRepository.updateAvailableVersion(null, game)
    }

    fun togglePlaying(game: Game) = viewModelScope.launch {
        gamesRepository.updatePlayState(
            if (game.playState == PlayState.Playing)
                PlayState.None
            else
                PlayState.Playing,
            game
        )
    }

    fun toggleCompleted(game: Game) = viewModelScope.launch {
        gamesRepository.updatePlayState(
            if (game.playState == PlayState.Completed)
                PlayState.None
            else
                PlayState.Completed,
            game
        )
    }

    fun toggleWaitingForUpdate(game: Game) = viewModelScope.launch {
        gamesRepository.updatePlayState(
            if (game.playState == PlayState.WaitingForUpdate)
                PlayState.None
            else
                PlayState.WaitingForUpdate,
            game
        )
    }

    fun toggleHidden(game: Game) = viewModelScope.launch {
        gamesRepository.updateHidden(!game.hidden, game)
    }

    fun updateRating(rating: Int, game: Game) = viewModelScope.launch {
        gamesRepository.updateRating(rating, game)
    }
}