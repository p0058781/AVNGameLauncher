package org.skynetsoftware.avnlauncher.ui.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.data.UpdateChecker
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.model.PlayState
import org.skynetsoftware.avnlauncher.data.repository.Filter
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.data.repository.SortDirection
import org.skynetsoftware.avnlauncher.data.repository.SortOrder
import org.skynetsoftware.avnlauncher.launcher.GameLauncher
import org.skynetsoftware.avnlauncher.settings.SettingsManager
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.utils.ExecutableFinder

class GamesViewModel(
    private val gamesRepository: GamesRepository,
    private val settingsManager: SettingsManager,
    private val updateChecker: UpdateChecker,
    private val gameLauncher: GameLauncher,
    private val eventCenter: EventCenter,
    private val executableFinder: ExecutableFinder,
) : ViewModel() {
    private val repoGames = gamesRepository.games.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val searchQuery = MutableStateFlow("")

    val filter: StateFlow<Filter> = settingsManager.selectedFilter
    val sortOrder: StateFlow<SortOrder> = settingsManager.selectedSortOrder
    val sortDirection: StateFlow<SortDirection> = settingsManager.selectedSortDirection
    val games: StateFlow<List<Game>> =
        combine(repoGames, filter, sortOrder, sortDirection, searchQuery) { values ->
            @Suppress("UNCHECKED_CAST")
            val games = values[0] as List<Game>
            val filter = values[1] as Filter
            val sortOrder = values[2] as SortOrder
            val sortDirection = values[3] as SortDirection
            val searchQuery = values[4] as String
            sortOrder.sort(
                filter.filter(games).filter { game ->
                    if (searchQuery.isBlank()) true else game.title.lowercase().contains(searchQuery)
                },
                sortDirection,
            )
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val firstPlayedTime = repoGames.map { games ->
        games.minOfOrNull { it.added } ?: 0L
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0L)

    val totalPlayTime = repoGames.map { games ->
        games.sumOf { it.playTime }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0L)

    val averagePlayTime: StateFlow<Float> = combine(firstPlayedTime, totalPlayTime) { firstPlayedTime, totalPlayTime ->
        val now = Clock.System.now().toEpochMilliseconds()
        val totalTimeDays = (now - firstPlayedTime) / 86400000f
        val totalPlayTimeDays = totalPlayTime / 86400000f
        val dailyPlayTimeHours = (totalPlayTimeDays / totalTimeDays) * 24f
        dailyPlayTimeHours
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0f)

    val showExecutablePathPicker = MutableStateFlow<Game?>(null)

    private val _updateCheckComplete = MutableSharedFlow<List<UpdateChecker.UpdateResult>>(replay = 0)
    val updateCheckComplete: SharedFlow<List<UpdateChecker.UpdateResult>?> get() = _updateCheckComplete

    init {
        // check paths
        viewModelScope.launch {
            gamesRepository.refresh()
            val gamesToUpdate = executableFinder.validateExecutables(gamesRepository.all())
            gamesRepository.updateExecutablePaths(gamesToUpdate)
        }
    }

    fun setFilter(filter: Filter) =
        viewModelScope.launch {
            settingsManager.setSelectedFilter(filter)
        }

    fun setSortOrder(sortOrder: SortOrder) =
        viewModelScope.launch {
            settingsManager.setSelectedSortOrder(sortOrder)
        }

    fun setSortDirection(sortDirection: SortDirection) =
        viewModelScope.launch {
            settingsManager.setSelectedSortDirection(sortDirection)
        }

    fun resetUpdateAvailable(
        availableVersion: String,
        game: Game,
    ) = viewModelScope.launch {
        gamesRepository.updateGame(game.f95ZoneThreadId, false, availableVersion, null)
    }

    fun togglePlaying(game: Game) =
        viewModelScope.launch {
            gamesRepository.updatePlayState(
                game.f95ZoneThreadId,
                if (game.playState == PlayState.Playing) {
                    PlayState.None
                } else {
                    PlayState.Playing
                },
            )
        }

    fun toggleCompleted(game: Game) =
        viewModelScope.launch {
            gamesRepository.updatePlayState(
                game.f95ZoneThreadId,
                if (game.playState == PlayState.Completed) {
                    PlayState.None
                } else {
                    PlayState.Completed
                },
            )
        }

    fun toggleWaitingForUpdate(game: Game) =
        viewModelScope.launch {
            gamesRepository.updatePlayState(
                game.f95ZoneThreadId,
                if (game.playState == PlayState.WaitingForUpdate) {
                    PlayState.None
                } else {
                    PlayState.WaitingForUpdate
                },
            )
        }

    fun toggleHidden(game: Game) =
        viewModelScope.launch {
            gamesRepository.updateHidden(game.f95ZoneThreadId, !game.hidden)
        }

    fun updateRating(
        rating: Int,
        game: Game,
    ) = viewModelScope.launch {
        gamesRepository.updateRating(game.f95ZoneThreadId, rating)
    }

    fun startUpdateCheck() {
        updateChecker.startUpdateCheck(true) { updateResult ->
            viewModelScope.launch {
                _updateCheckComplete.emit(updateResult)
            }
        }
    }

    fun launchGame(game: Game) =
        viewModelScope.launch {
            val executablePaths = game.executablePaths
            if (game.executablePaths.isEmpty()) {
                eventCenter.emit(Event.ToastMessage(MR.strings.gameLauncherInvalidExecutableToast, game.title))
                return@launch
            } else if (executablePaths.size > 1) {
                showExecutablePathPicker.emit(game)
            } else {
                gameLauncher.launch(game, executablePaths.first())
            }
        }

    fun launchGame(
        game: Game,
        executablePath: String,
    ) = viewModelScope.launch {
        showExecutablePathPicker.emit(null)
        gameLauncher.launch(game, executablePath)
    }
}
