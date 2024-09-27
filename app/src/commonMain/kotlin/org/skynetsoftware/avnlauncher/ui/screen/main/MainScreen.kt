package org.skynetsoftware.avnlauncher.ui.screen.main

import androidx.compose.foundation.text.BasicText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.SolidColor
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import com.dokar.sonner.LocalToastContentColor
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import kotlinx.coroutines.Dispatchers
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
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.State
import org.skynetsoftware.avnlauncher.ui.screen.PickExecutableDialog
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModel
import org.skynetsoftware.avnlauncher.updatechecker.UpdateCheckResult
import org.skynetsoftware.avnlauncher.updatechecker.buildToastMessage
import org.skynetsoftware.avnlauncher.utils.collectAsMutableState
import java.text.SimpleDateFormat

@OptIn(ExperimentalCoilApi::class)
@Composable
fun MainScreen(
    gamesViewModel: MainScreenViewModel = viewModel(),
    imageLoaderFactory: ImageLoaderFactory = koinInject(),
) {
    val games by remember { gamesViewModel.games }.collectAsState()
    val currentFilter by remember { gamesViewModel.selectedFilter }.collectAsState(Filter.All)
    val filters by remember { gamesViewModel.filters }.collectAsState(emptyList<FilterViewItem>())
    val currentSortOrder by remember { gamesViewModel.sortOrder }.collectAsState()
    val currentSortDirection by remember { gamesViewModel.sortDirection }.collectAsState()
    val currentGamesDisplayMode by remember { gamesViewModel.gamesDisplayMode }.collectAsState()
    val sfwMode by remember { gamesViewModel.sfwMode }.collectAsState()

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

    val gameRunning by remember { gamesViewModel.gameRunning }.collectAsState()

    setSingletonImageLoaderFactory { context ->
        imageLoaderFactory.createImageLoader(false, context)
    }

    val toasterState = rememberToasterState()

    LaunchedEffect(null) {
        gamesViewModel.toastMessage.collect {
            it?.let { toastMessage ->
                toasterState.show(
                    message = toastMessage,
                )
            }
        }
    }


        MainScreenContent(
            games = games,
            runningGame = gameRunning,
            currentFilter = currentFilter,
            filters = filters,
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
            stopGame = gamesViewModel::stopGame,
            resetUpdateAvailable = gamesViewModel::resetUpdateAvailable,
            updateRating = gamesViewModel::updateRating,
        )


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

    @Suppress("SpreadOperator")
    Toaster(
        state = toasterState,
        showCloseButton = true,
        darkTheme = true,
        messageSlot = { toast ->
            val message = toast.message as? Event.ToastMessage<*>
            val text = when (message?.message) {
                is String -> message.message
                is StringResource -> stringResource(message.message, *message.args)
                is UpdateCheckResult -> message.message.buildToastMessage()
                else -> null
            }
            if (text != null) {
                val contentColor = LocalToastContentColor.current
                BasicText(text, color = { contentColor })
            }
        },
        background = {
            SolidColor(MaterialTheme.colors.surface)
        },
    )
}



@Composable
expect fun MainScreenContent(
    games: List<Game>,
    runningGame: Game?,
    currentFilter: Filter,
    filters: List<FilterViewItem>,
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
    stopGame: () -> Unit,
    resetUpdateAvailable: (availableVersion: String, game: Game) -> Unit,
    updateRating: (rating: Int, game: Game) -> Unit,
)
