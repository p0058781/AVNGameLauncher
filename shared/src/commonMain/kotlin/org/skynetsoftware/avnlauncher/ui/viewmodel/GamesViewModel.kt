package org.skynetsoftware.avnlauncher.ui.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.model.PlayState
import org.skynetsoftware.avnlauncher.data.repository.Filter
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.data.repository.SortDirection
import org.skynetsoftware.avnlauncher.data.repository.SortOrder
import org.skynetsoftware.avnlauncher.logging.Logger
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.settings.SettingsManager
import org.skynetsoftware.avnlauncher.utils.formatPlayTime

class GamesViewModel(
    private val gamesRepository: GamesRepository,
    settingsManager: SettingsManager,
    logger: Logger
) : ViewModel() {

    val games: StateFlow<List<Game>> = gamesRepository.games.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val filter: StateFlow<Filter> = gamesRepository.filter
    val sortOrder: StateFlow<SortOrder> = gamesRepository.sortOrder
    val sortDirection: StateFlow<SortDirection> = gamesRepository.sortDirection

    val totalPlayTime: StateFlow<String> = gamesRepository.totalPlayTime.map {
        formatPlayTime(it)
    }.stateIn(viewModelScope, SharingStarted.Lazily, R.strings.noValue)

    val averagePlayTime: StateFlow<Float> =
        combine(gamesRepository.firstPlayedTime, gamesRepository.totalPlayTime) { firstPlayedTime, totalPlayTime ->
            val now = Clock.System.now().toEpochMilliseconds()
            val totalTimeDays = (now - firstPlayedTime) / 86400000f
            val totalPlayTimeDays = totalPlayTime / 86400000f
            val dailyPlayTimeHours = (totalPlayTimeDays / totalTimeDays) * 24f
            dailyPlayTimeHours
        }.stateIn(viewModelScope, SharingStarted.Lazily, 0f)

    init {
        //check paths
        viewModelScope.launch {
            validateExecutables(gamesRepository, settingsManager, logger)
        }
    }

    fun setFilter(filter: Filter) = viewModelScope.launch {
        gamesRepository.setFilter(filter)
    }

    fun setSortOrder(sortOrder: SortOrder) = viewModelScope.launch {
        gamesRepository.setSortOrder(sortOrder)
    }

    fun setSortDirection(sortDirection: SortDirection) = viewModelScope.launch {
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

expect suspend fun validateExecutables(
    gamesRepository: GamesRepository,
    settingsManager: SettingsManager,
    logger: Logger
)