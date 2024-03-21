package org.skynetsoftware.avnlauncher.ui.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.LocalExitApplication
import org.skynetsoftware.avnlauncher.LocalNavigator
import org.skynetsoftware.avnlauncher.LocalWindowControl
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.close
import org.skynetsoftware.avnlauncher.app.generated.resources.import
import org.skynetsoftware.avnlauncher.app.generated.resources.maximize
import org.skynetsoftware.avnlauncher.app.generated.resources.minimize
import org.skynetsoftware.avnlauncher.app.generated.resources.refresh
import org.skynetsoftware.avnlauncher.app.generated.resources.settings
import org.skynetsoftware.avnlauncher.app.generated.resources.toolbarActionNsfw
import org.skynetsoftware.avnlauncher.app.generated.resources.toolbarActionSfw
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.GamesDisplayMode
import org.skynetsoftware.avnlauncher.domain.model.GridColumns
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder
import org.skynetsoftware.avnlauncher.state.State
import org.skynetsoftware.avnlauncher.state.buildText
import org.skynetsoftware.avnlauncher.ui.component.IconAction
import org.skynetsoftware.avnlauncher.ui.component.Search
import org.skynetsoftware.avnlauncher.ui.component.TextAction
import org.skynetsoftware.avnlauncher.ui.screen.main.games.Games
import java.text.SimpleDateFormat

@Composable
private fun Toolbar(
    globalState: State,
    sfwMode: Boolean,
    totalPlayTime: Long,
    averagePlayTime: Float,
    searchQuery: String,
    setSearchQuery: (searchQuery: String) -> Unit,
    startUpdateCheck: () -> Unit,
    onSfwModeClicked: () -> Unit,
) {
    val draggableArea = LocalWindowControl.current?.draggableArea
    if (draggableArea != null) {
        draggableArea.invoke {
            ToolbarInternal(
                globalState = globalState,
                sfwMode = sfwMode,
                totalPlayTime = totalPlayTime,
                averagePlayTime = averagePlayTime,
                searchQuery = searchQuery,
                setSearchQuery = setSearchQuery,
                startUpdateCheck = startUpdateCheck,
                onSfwModeClicked = onSfwModeClicked,
            )
        }
    } else {
        ToolbarInternal(
            globalState = globalState,
            sfwMode = sfwMode,
            totalPlayTime = totalPlayTime,
            averagePlayTime = averagePlayTime,
            searchQuery = searchQuery,
            setSearchQuery = setSearchQuery,
            startUpdateCheck = startUpdateCheck,
            onSfwModeClicked = onSfwModeClicked,
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ToolbarInternal(
    globalState: State,
    sfwMode: Boolean,
    totalPlayTime: Long,
    averagePlayTime: Float,
    searchQuery: String,
    setSearchQuery: (searchQuery: String) -> Unit,
    startUpdateCheck: () -> Unit,
    onSfwModeClicked: () -> Unit,
) {
    var maximized by remember { mutableStateOf(false) }
    TopAppBar {
        val windowControl = LocalWindowControl.current
        ToolbarTitle(
            totalPlayTime = totalPlayTime,
            averagePlayTime = averagePlayTime,
        )
        Search(
            searchQuery = searchQuery,
            setSearchQuery = setSearchQuery,
        )

        if (globalState != State.Idle) {
            Text(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(end = 10.dp),
                textAlign = TextAlign.End,
                text = globalState.buildText(),
                style = MaterialTheme.typography.subtitle2,
            )
        } else {
            Spacer(
                modifier = Modifier.weight(1f).fillMaxWidth(),
            )
        }

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.End,
        ) {
            val exitApplication = LocalExitApplication.current
            val navigator = LocalNavigator.current
            TextAction(stringResource(if (sfwMode) Res.string.toolbarActionSfw else Res.string.toolbarActionNsfw)) {
                onSfwModeClicked()
            }
            IconAction(Res.drawable.import) {
                navigator?.navigateToImportGame()
            }
            IconAction(Res.drawable.refresh) {
                startUpdateCheck()
            }
            IconAction(Res.drawable.settings) {
                navigator?.navigateToSettings()
            }
            IconAction(if (maximized) Res.drawable.minimize else Res.drawable.maximize) {
                maximized = !maximized
                if (maximized) {
                    windowControl?.maximizeWindow?.invoke()
                } else {
                    windowControl?.floatingWindow?.invoke()
                }
            }
            IconAction(Res.drawable.close) {
                exitApplication?.invoke()
            }
        }
    }
}

@Composable
actual fun MainScreenContent(
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
) {
    val navigator = LocalNavigator.current
    Column {
        Toolbar(
            globalState = globalState,
            sfwMode = sfwMode,
            totalPlayTime = totalPlayTime,
            averagePlayTime = averagePlayTime,
            searchQuery = searchQuery,
            setSearchQuery = setSearchQuery,
            startUpdateCheck = startUpdateCheck,
            onSfwModeClicked = toggleSfwMode,
        )
        SortFilter(
            games = games,
            currentFilter = currentFilter,
            currentSortOrder = currentSortOrder,
            currentSortDirection = currentSortDirection,
            currentGamesDisplayMode = currentGamesDisplayMode,
            updateAvailableIndicatorVisible = newUpdateAvailableIndicatorVisible,
            modifier = Modifier.align(Alignment.End).padding(10.dp),
            setFilter = setFilter,
            setSortOrder = setSortOrder,
            setSortDirection = setSortDirection,
            setGamesDisplayMode = setGamesDisplayMode,
        )
        Games(
            games = games,
            sfwMode = sfwMode,
            query = searchQuery,
            imageAspectRatio = imageAspectRatio,
            dateFormat = dateFormat,
            timeFormat = timeFormat,
            gridColumns = gridColumns,
            gamesDisplayMode = currentGamesDisplayMode,
            editGame = {
                navigator?.navigateToEditGame(it)
            },
            launchGame = launchGame,
            resetUpdateAvailable = resetUpdateAvailable,
            updateRating = updateRating,
            updateFavorite = updateFavorite,
        )
    }
}
