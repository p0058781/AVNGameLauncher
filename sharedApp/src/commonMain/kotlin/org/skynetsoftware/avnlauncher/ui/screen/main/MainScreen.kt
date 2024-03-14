package org.skynetsoftware.avnlauncher.ui.screen.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.skynetsoftware.avnlauncher.LocalDraggableArea
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.state.State
import org.skynetsoftware.avnlauncher.ui.component.OutlinedTextFieldWithPadding
import org.skynetsoftware.avnlauncher.ui.component.Toast
import org.skynetsoftware.avnlauncher.ui.screen.PickExecutableDialog
import org.skynetsoftware.avnlauncher.ui.screen.editgame.EditGameDialog
import org.skynetsoftware.avnlauncher.ui.screen.import.ImportGameDialog
import org.skynetsoftware.avnlauncher.ui.screen.main.games.Games
import org.skynetsoftware.avnlauncher.ui.screen.settings.SettingsDialog
import org.skynetsoftware.avnlauncher.ui.viewmodel.GamesScreenModel
import org.skynetsoftware.avnlauncher.ui.viewmodel.MainScreenModel
import org.skynetsoftware.avnlauncher.updatechecker.UpdateCheckResult
import org.skynetsoftware.avnlauncher.utils.collectAsMutableState
import org.skynetsoftware.avnlauncher.utils.formatPlayTime

data class MainScreen(
    val exitApplication: () -> Unit,
    val setMaximized: () -> Unit,
    val setFloating: () -> Unit,
) : Screen {
    @OptIn(ExperimentalResourceApi::class)
    @Suppress("LongMethod")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val mainScreenModel: MainScreenModel = navigator.getNavigatorScreenModel()
        val gamesScreenModel: GamesScreenModel = getScreenModel()
        val games by remember { gamesScreenModel.games }.collectAsState()
        val currentFilter by remember { gamesScreenModel.filter }.collectAsState()
        val currentSortOrder by remember { gamesScreenModel.sortOrder }.collectAsState()
        val currentSortDirection by remember { gamesScreenModel.sortDirection }.collectAsState()
        val currentGamesDisplayMode by remember { gamesScreenModel.gamesDisplayMode }.collectAsState()
        val sfwMode by remember { mainScreenModel.sfwMode }.collectAsState()
        val maximized by remember { mainScreenModel.windowMaximized }.collectAsState()

        var selectedGame by remember { mutableStateOf<Game?>(null) }
        val toastMessage by remember { mainScreenModel.toastMessage }.collectAsState()
        var showExecutablePathPicker by gamesScreenModel.showExecutablePathPicker.collectAsMutableState()

        val totalPlayTime by remember { gamesScreenModel.totalPlayTime }.collectAsState()
        val averagePlayTime by remember { gamesScreenModel.averagePlayTime }.collectAsState()
        val newUpdateAvailableIndicatorVisible by remember {
            mainScreenModel.newUpdateAvailableIndicatorVisible
        }.collectAsState()
        var searchQuery by remember { gamesScreenModel.searchQuery }
            .collectAsMutableState(context = Dispatchers.Main.immediate)
        val globalState by remember { mainScreenModel.state }.collectAsState()
        var importGameDialogVisible by remember { mutableStateOf(false) }
        var settingsDialogVisible by remember { mutableStateOf(false) }

        val draggableArea = LocalDraggableArea.current
        Column {
            draggableArea?.invoke {
                TopAppBar {
                    ToolbarTitle {
                        Column(
                            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                        ) {
                            Text(
                                text = stringResource(MR.strings.appName),
                                style = MaterialTheme.typography.h6,
                                color = MaterialTheme.colors.onPrimary,
                            )
                            Text(
                                stringResource(
                                    MR.strings.totalPlayTime,
                                    formatPlayTime(totalPlayTime),
                                    averagePlayTime,
                                ),
                            )
                        }
                    }
                    ToolbarSearch {
                        Box(
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                        ) {
                            OutlinedTextFieldWithPadding(
                                modifier = Modifier.padding(0.dp).fillMaxHeight(),
                                value = searchQuery,
                                onValueChange = {
                                    searchQuery = it
                                },
                                placeholder = {
                                    Text(
                                        text = stringResource(MR.strings.searchLabel),
                                        style = MaterialTheme.typography.body2,
                                    )
                                },
                                textStyle = TextStyle(
                                    color = MaterialTheme.colors.onPrimary,
                                ),
                                singleLine = true,
                                trailingIcon = {
                                    if (searchQuery.isNotBlank()) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "clear text",
                                            modifier = Modifier.clickable {
                                                searchQuery = ""
                                            },
                                        )
                                    }
                                },
                                contentPadding = PaddingValues(8.dp),
                            )
                        }
                    }
                    ToolbarState {
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
                    }
                    ToolbarActions(
                        sfwMode = sfwMode,
                        maximized = maximized,
                        startUpdateCheck = {
                            mainScreenModel.startUpdateCheck()
                        },
                        onImportGameClicked = {
                            importGameDialogVisible = true
                        },
                        onSettingsClicked = {
                            settingsDialogVisible = true
                        },
                        onSfwModeClicked = {
                            mainScreenModel.toggleSfwMode()
                        },
                        onToggleMaximizedClicked = {
                            mainScreenModel.toggleMaximized(setMaximized, setFloating)
                        },
                        exitApplication = exitApplication,
                    )
                }
            }
            SortFilter(
                games = games,
                currentFilter = currentFilter,
                currentSortOrder = currentSortOrder,
                currentSortDirection = currentSortDirection,
                currentGamesDisplayMode = currentGamesDisplayMode,
                updateAvailableIndicatorVisible = newUpdateAvailableIndicatorVisible,
                modifier = Modifier.align(Alignment.End).padding(10.dp),
                setFilter = {
                    gamesScreenModel.setFilter(it)
                    if (it == org.skynetsoftware.avnlauncher.domain.model.Filter.GamesWithUpdate) {
                        mainScreenModel.resetNewUpdateAvailableIndicatorVisible()
                    }
                },
                setSortOrder = {
                    gamesScreenModel.setSortOrder(it)
                },
                setSortDirection = {
                    gamesScreenModel.setSortDirection(it)
                },
                setGamesDisplayMode = {
                    gamesScreenModel.setGamesDisplayMode(it)
                },
            )
            Games(
                games = games,
                sfwMode = sfwMode,
                query = searchQuery,
                gamesDisplayMode = currentGamesDisplayMode,
                editGame = {
                    selectedGame = it
                },
                launchGame = {
                    gamesScreenModel.launchGame(it)
                },
                resetUpdateAvailable = { availableVersion, game ->
                    gamesScreenModel.resetUpdateAvailable(availableVersion, game)
                },
                updateRating = { rating, game ->
                    gamesScreenModel.updateRating(rating, game)
                },
                updateFavorite = { favorite, game ->
                    gamesScreenModel.updateFavorite(favorite, game)
                },
            )
        }
        selectedGame?.let {
            EditGameDialog(
                editGameScreenModel = koinInject(parameters = { parametersOf(it) }),
                onCloseRequest = {
                    selectedGame = null
                },
            )
        }
        showExecutablePathPicker?.let { game ->
            PickExecutableDialog(
                executablePaths = game.executablePaths,
                onCloseRequest = {
                    showExecutablePathPicker = null
                },
                onExecutablePicked = { executablePath ->
                    showExecutablePathPicker = null
                    gamesScreenModel.launchGame(game, executablePath)
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
        if (importGameDialogVisible) {
            ImportGameDialog(
                onCloseRequest = {
                    importGameDialogVisible = false
                },
            )
        }
        if (settingsDialogVisible) {
            SettingsDialog(
                onCloseRequest = {
                    settingsDialogVisible = false
                },
            )
        }
    }
}

@Composable
private fun State.buildText() =
    buildString {
        when (val state = this@buildText) {
            State.Idle -> append(stringResource(MR.strings.stateIdle))
            is State.Playing -> append(stringResource(MR.strings.statePlaying, state.game.title))
            State.UpdateCheckRunning -> append(stringResource(MR.strings.stateCheckingForUpdates))
        }
    }

@Composable
fun UpdateCheckResult.buildToastMessage(): String {
    return stringResource(MR.strings.updateCheckUpdateAvailable, updates.size, exceptions.size)
}
