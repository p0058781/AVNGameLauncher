package org.skynetsoftware.avnlauncher.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.launcher.GameLauncher
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.utils.ExecutableFinder
import org.skynetsoftware.avnlauncher.utils.calculateAveragePlayTime

@Suppress("TooManyFunctions")
class GamesScreenModel(
    private val gamesRepository: GamesRepository,
    private val settingsRepository: SettingsRepository,
    private val gameLauncher: GameLauncher,
    private val eventCenter: EventCenter,
    private val executableFinder: ExecutableFinder,
) : ScreenModel {
    private val repoGames = gamesRepository.games.stateIn(screenModelScope, SharingStarted.Lazily, emptyList())

    val searchQuery = MutableStateFlow("")

    val filter: StateFlow<Filter> = settingsRepository.selectedFilter
    val sortOrder: StateFlow<SortOrder> = settingsRepository.selectedSortOrder
    val sortDirection: StateFlow<SortDirection> = settingsRepository.selectedSortDirection
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
        }.stateIn(screenModelScope, SharingStarted.Lazily, emptyList())

    private val firstPlayedTime = repoGames.map { games ->
        games.minOfOrNull { it.firstPlayed } ?: 0L
    }.stateIn(screenModelScope, SharingStarted.Lazily, 0L)

    val totalPlayTime = repoGames.map { games ->
        games.sumOf { it.playTime }
    }.stateIn(screenModelScope, SharingStarted.Lazily, 0L)

    val averagePlayTime: StateFlow<Float> = combine(firstPlayedTime, totalPlayTime) { firstPlayedTime, totalPlayTime ->
        calculateAveragePlayTime(firstPlayedTime, totalPlayTime)
    }.stateIn(screenModelScope, SharingStarted.Lazily, 0f)

    val showExecutablePathPicker = MutableStateFlow<Game?>(null)

    init {
        // check paths
        screenModelScope.launch {
            val gamesToUpdate = executableFinder.validateExecutables(gamesRepository.all())
            gamesRepository.updateExecutablePaths(gamesToUpdate)
        }
    }

    fun setFilter(filter: Filter) =
        screenModelScope.launch {
            settingsRepository.setSelectedFilter(filter)
        }

    fun setSortOrder(sortOrder: SortOrder) =
        screenModelScope.launch {
            settingsRepository.setSelectedSortOrder(sortOrder)
        }

    fun setSortDirection(sortDirection: SortDirection) =
        screenModelScope.launch {
            settingsRepository.setSelectedSortDirection(sortDirection)
        }

    fun resetUpdateAvailable(
        availableVersion: String,
        game: Game,
    ) = screenModelScope.launch {
        gamesRepository.updateGame(game.f95ZoneThreadId, false, availableVersion, null)
    }

    fun updateRating(
        rating: Int,
        game: Game,
    ) = screenModelScope.launch {
        gamesRepository.updateRating(game.f95ZoneThreadId, rating)
    }

    fun updateFavorite(
        favorite: Boolean,
        game: Game,
    ) = screenModelScope.launch {
        gamesRepository.updateFavorite(game.f95ZoneThreadId, favorite)
    }

    fun launchGame(game: Game) =
        screenModelScope.launch {
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
    ) = screenModelScope.launch {
        showExecutablePathPicker.emit(null)
        gameLauncher.launch(game, executablePath)
    }
}
