package org.skynetsoftware.avnlauncher.ui.screen.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.seiko.imageloader.LocalImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.GamesDisplayMode
import org.skynetsoftware.avnlauncher.domain.model.GridColumns
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder
import org.skynetsoftware.avnlauncher.imageloader.ImageLoaderFactory
import org.skynetsoftware.avnlauncher.state.State
import org.skynetsoftware.avnlauncher.ui.component.Toast
import org.skynetsoftware.avnlauncher.ui.screen.PickExecutableDialog
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModel
import org.skynetsoftware.avnlauncher.updatechecker.UpdateCheckResult
import org.skynetsoftware.avnlauncher.updatechecker.buildToastMessage
import org.skynetsoftware.avnlauncher.utils.collectAsMutableState
import java.text.SimpleDateFormat

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MainScreen(
    gamesViewModel: MainScreenViewModel = viewModel(),
    imageLoaderFactory: ImageLoaderFactory = koinInject(),
) {
    val games by remember { gamesViewModel.games }.collectAsState()
    val currentFilter by remember { gamesViewModel.filter }.collectAsState()
    val currentSortOrder by remember { gamesViewModel.sortOrder }.collectAsState()
    val currentSortDirection by remember { gamesViewModel.sortDirection }.collectAsState()
    val currentGamesDisplayMode by remember { gamesViewModel.gamesDisplayMode }.collectAsState()
    val sfwMode by remember { gamesViewModel.sfwMode }.collectAsState()

    val toastMessage by remember { gamesViewModel.toastMessage }.collectAsState()
    var showExecutablePathPicker by gamesViewModel.showExecutablePathPicker.collectAsMutableState()

    val totalPlayTime by remember { gamesViewModel.totalPlayTime }.collectAsState()
    val averagePlayTime by remember { gamesViewModel.averagePlayTime }.collectAsState()
    val newUpdateAvailableIndicatorVisible by remember {
        gamesViewModel.newUpdateAvailableIndicatorVisible
    }.collectAsState()
    var searchQuery by remember { gamesViewModel.searchQuery }
        .collectAsMutableState(context = Dispatchers.Main.immediate)
    val globalState by remember { gamesViewModel.state }.collectAsState()
    val imageAspectRatio by remember { gamesViewModel.imageAspectRatio }.collectAsState()
    val dateFormat by remember { gamesViewModel.dateFormat }.collectAsState()
    val timeFormat by remember { gamesViewModel.timeFormat }.collectAsState()
    val gridColumns by remember { gamesViewModel.gridColumns }.collectAsState()

    val imageLoader by remember {
        gamesViewModel.showGifs.map { imageLoaderFactory.createImageLoader(it) }
    }.collectAsState(imageLoaderFactory.createImageLoader(false))

    CompositionLocalProvider(
        LocalImageLoader provides imageLoader,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            MainScreenContent(
                games = games,
                currentFilter = currentFilter,
                currentSortOrder = currentSortOrder,
                currentSortDirection = currentSortDirection,
                currentGamesDisplayMode = currentGamesDisplayMode,
                newUpdateAvailableIndicatorVisible = newUpdateAvailableIndicatorVisible,
                globalState = globalState,
                sfwMode = sfwMode,
                totalPlayTime = totalPlayTime,
                averagePlayTime,
                searchQuery,
                imageAspectRatio = imageAspectRatio,
                dateFormat = SimpleDateFormat(dateFormat),
                timeFormat = SimpleDateFormat(timeFormat),
                gridColumns = gridColumns,
                setSearchQuery = {
                    searchQuery = it
                },
                startUpdateCheck = gamesViewModel::startUpdateCheck,
                toggleSfwMode = gamesViewModel::toggleSfwMode,
                setFilter = {
                    gamesViewModel.setFilter(it)
                    if (it == Filter.GamesWithUpdate) {
                        gamesViewModel.resetNewUpdateAvailableIndicatorVisible()
                    }
                },
                setSortOrder = gamesViewModel::setSortOrder,
                setSortDirection = gamesViewModel::setSortDirection,
                setGamesDisplayMode = gamesViewModel::setGamesDisplayMode,
                launchGame = gamesViewModel::launchGame,
                resetUpdateAvailable = gamesViewModel::resetUpdateAvailable,
                updateRating = gamesViewModel::updateRating,
                updateFavorite = gamesViewModel::updateFavorite,
            )
        }
    }
    showExecutablePathPicker?.let { game ->
        PickExecutableDialog(
            executablePaths = game.executablePaths,
            onCloseRequest = {
                showExecutablePathPicker = null
            },
            onExecutablePicked = { executablePath ->
                showExecutablePathPicker = null
                gamesViewModel.launchGame(game, executablePath)
            },
        )
    }
    toastMessage?.let {
        @Suppress("SpreadOperator")
        val message = when (it.message) {
            is String -> it.message
            is StringResource -> stringResource(it.message, *it.args)
            is UpdateCheckResult -> it.message.buildToastMessage()
            else -> null
        } ?: return@let
        Toast(
            text = message,
        )
    }
}

@Composable
expect fun MainScreenContent(
    games: List<Game>,
    currentFilter: Filter,
    currentSortOrder: SortOrder,
    currentSortDirection: SortDirection,
    currentGamesDisplayMode: GamesDisplayMode,
    newUpdateAvailableIndicatorVisible: Boolean,
    globalState: State,
    sfwMode: Boolean,
    totalPlayTime: Long,
    averagePlayTime: Float,
    searchQuery: String,
    imageAspectRatio: Float,
    dateFormat: SimpleDateFormat,
    timeFormat: SimpleDateFormat,
    gridColumns: GridColumns,
    setSearchQuery: (searchQuery: String) -> Unit,
    startUpdateCheck: () -> Unit,
    toggleSfwMode: () -> Unit,
    setFilter: (filter: Filter) -> Unit,
    setSortOrder: (sortOrder: SortOrder) -> Unit,
    setSortDirection: (sortDirection: SortDirection) -> Unit,
    setGamesDisplayMode: (gamesDisplayMode: GamesDisplayMode) -> Unit,
    launchGame: (game: Game) -> Unit,
    resetUpdateAvailable: (availableVersion: String, game: Game) -> Unit,
    updateRating: (rating: Int, game: Game) -> Unit,
    updateFavorite: (favorite: Boolean, game: Game) -> Unit,
)
