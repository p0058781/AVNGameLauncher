package org.skynetsoftware.avnlauncher.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
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
import org.skynetsoftware.avnlauncher.LocalNavigator
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.import
import org.skynetsoftware.avnlauncher.app.generated.resources.more_vert
import org.skynetsoftware.avnlauncher.app.generated.resources.refresh
import org.skynetsoftware.avnlauncher.app.generated.resources.search
import org.skynetsoftware.avnlauncher.app.generated.resources.settings
import org.skynetsoftware.avnlauncher.app.generated.resources.toggle_off
import org.skynetsoftware.avnlauncher.app.generated.resources.toggle_on
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.GamesDisplayMode
import org.skynetsoftware.avnlauncher.domain.model.GridColumns
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder
import org.skynetsoftware.avnlauncher.state.State
import org.skynetsoftware.avnlauncher.state.buildText
import org.skynetsoftware.avnlauncher.ui.component.DropdownItemAction
import org.skynetsoftware.avnlauncher.ui.component.IconAction
import org.skynetsoftware.avnlauncher.ui.component.Search
import org.skynetsoftware.avnlauncher.ui.screen.main.games.Games
import java.text.SimpleDateFormat

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun Toolbar(
    sfwMode: Boolean,
    totalPlayTime: Long,
    averagePlayTime: Float,
    searchQuery: String,
    setSearchQuery: (searchQuery: String) -> Unit,
    startUpdateCheck: () -> Unit,
    onSfwModeClicked: () -> Unit,
) {
    var searchVisible by remember { mutableStateOf(false) }
    TopAppBar {
        if (!searchVisible) {
            ToolbarTitle(
                totalPlayTime = totalPlayTime,
                averagePlayTime = averagePlayTime,
            )
        }
        if (searchVisible) {
            Search(
                searchQuery = searchQuery,
                setSearchQuery = setSearchQuery,
            )
        }

        Spacer(
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.End,
        ) {
            var showDropdownMenu by remember { mutableStateOf(false) }
            val navigator = LocalNavigator.current

            IconAction(Res.drawable.more_vert) {
                showDropdownMenu = true
            }
            DropdownMenu(
                expanded = showDropdownMenu,
                onDismissRequest = {
                    showDropdownMenu = false
                },
            ) {
                DropdownItemAction(
                    text = "Search",
                    icon = Res.drawable.search,
                ) {
                    searchVisible = !searchVisible
                    showDropdownMenu = false
                }
                DropdownItemAction(
                    text = "SFW Mode",
                    icon = if (sfwMode) Res.drawable.toggle_on else Res.drawable.toggle_off,
                ) {
                    onSfwModeClicked()
                    showDropdownMenu = false
                }
                DropdownItemAction(
                    text = "Import Game",
                    icon = Res.drawable.import,
                ) {
                    navigator?.navigateToImportGame()
                    showDropdownMenu = false
                }
                DropdownItemAction(
                    text = "Check fo Updates",
                    icon = Res.drawable.refresh,
                ) {
                    startUpdateCheck()
                    showDropdownMenu = false
                }
                DropdownItemAction(
                    text = "Settings",
                    icon = Res.drawable.settings,
                ) {
                    navigator?.navigateToSettings()
                    showDropdownMenu = false
                }
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
    Surface(
        Modifier = blurModifier.fillMaxSize(),
    ) {
        val navigator = LocalNavigator.current
        Column {
            Toolbar(
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
            if (globalState != State.Idle) {
                Text(
                    modifier = Modifier.background(MaterialTheme.colors.surface).fillMaxWidth().padding(end = 10.dp),
                    textAlign = TextAlign.End,
                    text = globalState.buildText(),
                    style = MaterialTheme.typography.caption,
                )
            }
            Games(
                games = games,
                sfwMode = sfwMode,
                query = searchQuery,
                imageAspectRatio = imageAspectRatio,
                dateFormat = dateFormat,
                timeFormat = timeFormat,
                gridColumns = gridColumns,
                gamesDisplayMode = currentGamesDisplayMode,
                gameDetails = {
                    navigator?.navigateToGameDetails(it)
                },
                launchGame = launchGame,
                resetUpdateAvailable = resetUpdateAvailable,
                updateRating = updateRating,
                updateFavorite = updateFavorite,
            )
        }
    }
}
