package org.skynetsoftware.avnlauncher.ui.screen.main

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.gameLauncherInvalidExecutableToast
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.GamesDisplayMode
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.launcher.GameLauncher
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.state.State
import org.skynetsoftware.avnlauncher.state.StateHandler
import org.skynetsoftware.avnlauncher.ui.viewmodel.ShowToastViewModel
import org.skynetsoftware.avnlauncher.updatechecker.UpdateChecker
import org.skynetsoftware.avnlauncher.utils.ExecutableFinder
import org.skynetsoftware.avnlauncher.utils.calculateAveragePlayTime

@Suppress("TooManyFunctions", "LongParameterList")
class MainScreenViewModel(
    private val gamesRepository: GamesRepository,
    private val settingsRepository: SettingsRepository,
    private val gameLauncher: GameLauncher,
    private val eventCenter: EventCenter,
    private val executableFinder: ExecutableFinder,
    stateHandler: StateHandler,
    private val updateChecker: UpdateChecker,
) : ShowToastViewModel(eventCenter) {
    private val repoGames = gamesRepository.games.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val searchQuery = MutableStateFlow("")

    val filter: StateFlow<Filter> = settingsRepository.selectedFilter
    val sortOrder: StateFlow<SortOrder> = settingsRepository.selectedSortOrder
    val sortDirection: StateFlow<SortDirection> = settingsRepository.selectedSortDirection
    val gamesDisplayMode: StateFlow<GamesDisplayMode> = settingsRepository.selectedGamesDisplayMode
    val games: StateFlow<List<Game>> =
        combine(repoGames, filter, sortOrder, sortDirection, searchQuery) { values ->
            @Suppress("UNCHECKED_CAST", "MagicNumber")
            val games = values[0] as List<Game>
            val filter = values[1] as Filter
            val sortOrder = values[2] as SortOrder

            @Suppress("MagicNumber")
            val sortDirection = values[3] as SortDirection

            @Suppress("MagicNumber")
            val searchQuery = values[4] as String

            sortOrder.sort(
                filter.filter(games).filter { game ->
                    if (searchQuery.isBlank()) {
                        true
                    } else if (game.title.lowercase().contains(searchQuery.lowercase())) {
                        true
                    } else if (game.tags.any { it.lowercase().contains(searchQuery.lowercase()) }) {
                        true
                    } else {
                        false
                    }
                },
                sortDirection,
            )
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val firstPlayedTime = repoGames.map { games ->
        games.filter { it.firstPlayedTime > 0 }.minOfOrNull { it.firstPlayedTime } ?: 0L
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0L)

    private val lastPlayedTime = repoGames.map { games ->
        games.maxOfOrNull { it.lastPlayedTime } ?: 0L
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0L)

    val totalPlayTime = repoGames.map { games ->
        games.sumOf { it.totalPlayTime }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0L)

    val averagePlayTime: StateFlow<Float> = combine(
        firstPlayedTime,
        lastPlayedTime,
        totalPlayTime,
    ) { firstPlayedTime, lastPlayedTime, totalPlayTime ->
        calculateAveragePlayTime(firstPlayedTime, lastPlayedTime, totalPlayTime)
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0f)

    val showExecutablePathPicker = MutableStateFlow<Game?>(null)

    val state: StateFlow<State> = stateHandler.state
    val sfwMode = settingsRepository.sfwModeEnabled

    private val _toastMessage = MutableStateFlow<Event.ToastMessage<*>?>(null)
    val toastMessage: StateFlow<Event.ToastMessage<*>?> get() = _toastMessage

    private val _newUpdateAvailableIndicatorVisible = MutableStateFlow(false)
    val newUpdateAvailableIndicatorVisible: StateFlow<Boolean> get() = _newUpdateAvailableIndicatorVisible

    val imageAspectRatio = settingsRepository.gridImageAspectRatio
    val dateFormat = settingsRepository.dateFormat
    val timeFormat = settingsRepository.timeFormat
    val gridColumns = settingsRepository.gridColumns
    val showGifs = settingsRepository.showGifs

    init {
        // check paths
        viewModelScope.launch {
            val gamesToUpdate = executableFinder.validateExecutables(gamesRepository.all())
            gamesRepository.updateExecutablePaths(gamesToUpdate)
        }
        viewModelScope.launch {
            eventCenter.events.collect {
                when (it) {
                    is Event.ToastMessage<*> -> {
                        _toastMessage.emit(it)
                        delay(it.duration)
                        _toastMessage.emit(null)
                    }
                    is Event.UpdateCheckComplete -> {
                        if (it.updateCheckResult.updates.count { game -> game.updateAvailable } > 0) {
                            _newUpdateAvailableIndicatorVisible.emit(true)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun setFilter(filter: Filter) =
        viewModelScope.launch {
            settingsRepository.setSelectedFilter(filter)
        }

    fun setSortOrder(sortOrder: SortOrder) =
        viewModelScope.launch {
            settingsRepository.setSelectedSortOrder(sortOrder)
        }

    fun setSortDirection(sortDirection: SortDirection) =
        viewModelScope.launch {
            settingsRepository.setSelectedSortDirection(sortDirection)
        }

    fun setGamesDisplayMode(gamesDisplayMode: GamesDisplayMode) =
        viewModelScope.launch {
            settingsRepository.setSelectedGamesDisplayMode(gamesDisplayMode)
        }

    fun resetUpdateAvailable(
        availableVersion: String,
        game: Game,
    ) = viewModelScope.launch {
        gamesRepository.updateGame(game.f95ZoneThreadId, false, availableVersion, null)
    }

    fun updateRating(
        rating: Int,
        game: Game,
    ) = viewModelScope.launch {
        gamesRepository.updateRating(game.f95ZoneThreadId, rating)
    }

    fun updateFavorite(
        favorite: Boolean,
        game: Game,
    ) = viewModelScope.launch {
        gamesRepository.updateFavorite(game.f95ZoneThreadId, favorite)
    }

    @OptIn(ExperimentalResourceApi::class)
    fun launchGame(game: Game) =
        viewModelScope.launch {
            val executablePaths = game.executablePaths
            if (game.executablePaths.isEmpty()) {
                eventCenter.emit(Event.ToastMessage(Res.string.gameLauncherInvalidExecutableToast, game.title))
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

    fun toggleSfwMode() =
        viewModelScope.launch {
            settingsRepository.setSfwModeEnabled(!sfwMode.value)
        }

    fun startUpdateCheck() {
        viewModelScope.launch {
            val updateCheckResult = updateChecker.checkForUpdates(this)
            eventCenter.emit(Event.ToastMessage(updateCheckResult))
        }
    }

    fun resetNewUpdateAvailableIndicatorVisible() =
        viewModelScope.launch {
            _newUpdateAvailableIndicatorVisible.emit(false)
            eventCenter.emit(Event.UpdateSeen)
        }
}
