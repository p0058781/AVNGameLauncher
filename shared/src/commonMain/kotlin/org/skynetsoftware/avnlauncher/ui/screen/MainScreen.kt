package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.model.blur
import com.seiko.imageloader.rememberImagePainter
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.model.PlayState
import org.skynetsoftware.avnlauncher.data.repository.Filter
import org.skynetsoftware.avnlauncher.data.repository.SortDirection
import org.skynetsoftware.avnlauncher.data.repository.SortOrder
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.state.State
import org.skynetsoftware.avnlauncher.ui.component.RatingBar
import org.skynetsoftware.avnlauncher.ui.component.Toast
import org.skynetsoftware.avnlauncher.ui.screen.editgame.EditGameDialog
import org.skynetsoftware.avnlauncher.ui.screen.import.ImportGameDialog
import org.skynetsoftware.avnlauncher.ui.screen.settings.SettingsDialog
import org.skynetsoftware.avnlauncher.ui.theme.darkColors
import org.skynetsoftware.avnlauncher.ui.theme.lightColors
import org.skynetsoftware.avnlauncher.ui.viewmodel.GamesViewModel
import org.skynetsoftware.avnlauncher.ui.viewmodel.MainViewModel
import org.skynetsoftware.avnlauncher.utils.SimpleDateFormat
import org.skynetsoftware.avnlauncher.utils.format
import org.skynetsoftware.avnlauncher.utils.formatPlayTime
import org.skynetsoftware.avnlauncher.utils.gamesGridCellMinSizeDp

private val releaseDateFormat = SimpleDateFormat("yyyy-MM-dd")

// TODO scaling is not working on desktop
// TODO write tests
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = koinInject(),
    gamesViewModel: GamesViewModel = koinInject(),
    exitApplication: () -> Unit,
    draggableArea: @Composable (content: @Composable () -> Unit) -> Unit,
) {
    val games by remember { gamesViewModel.games }.collectAsState()
    val totalPlayTime by remember { gamesViewModel.totalPlayTime }.collectAsState()
    val averagePlayTime by remember { gamesViewModel.averagePlayTime }.collectAsState()
    val currentFilter by remember { gamesViewModel.filter }.collectAsState()
    val currentSortOrder by remember { gamesViewModel.sortOrder }.collectAsState()
    val currentSortDirection by remember { gamesViewModel.sortDirection }.collectAsState()
    val globalState by remember { mainViewModel.state }.collectAsState()
    val sfwMode by remember { mainViewModel.sfwMode }.collectAsState()

    var selectedGame by remember { mutableStateOf<Game?>(null) }
    var importGameDialogVisible by remember { mutableStateOf(false) }
    var settingsDialogVisible by remember { mutableStateOf(false) }
    val toastMessage by remember { mainViewModel.toastMessage }.collectAsState()
    var searchQuery by remember { gamesViewModel.searchQuery }.collectAsMutableState(context = Dispatchers.Main.immediate)
    var showExecutablePathPicker by gamesViewModel.showExecutablePathPicker.collectAsMutableState()

    MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkColors else lightColors,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            // TODO pull to refresh for remote client
            Column {
                draggableArea {
                    TopAppBar {
                        ToolbarTitle {
                            Column(
                                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                            ) {
                                Text(
                                    text = R.strings.appName,
                                    style = MaterialTheme.typography.h6,
                                    color = MaterialTheme.colors.onSurface,
                                )
                                Text(R.strings.totalPlayTime.format(formatPlayTime(totalPlayTime), averagePlayTime))
                            }
                        }
                        ToolbarSearch {
                            Box(
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier.padding(0.dp).fillMaxHeight(),
                                    value = searchQuery,
                                    onValueChange = {
                                        searchQuery = it
                                    },
                                    placeholder = {
                                        Text(
                                            text = R.strings.searchLabel,
                                            style = MaterialTheme.typography.body2,
                                        )
                                    },
                                    textStyle = TextStyle(
                                        color = MaterialTheme.colors.onSurface,
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
                            remoteClientMode = mainViewModel.remoteClientMode,
                            sfwMode = sfwMode,
                            startUpdateCheck = {
                                gamesViewModel.startUpdateCheck()
                            },
                            onImportGameClicked = {
                                importGameDialogVisible = true
                            },
                            onSettingsClicked = {
                                settingsDialogVisible = true
                            },
                            onSfwModeClicked = {
                                mainViewModel.toggleSfwMode()
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
                    modifier = Modifier.align(Alignment.End).padding(10.dp),
                    setFilter = {
                        gamesViewModel.setFilter(it)
                    },
                    setSortOrder = {
                        gamesViewModel.setSortOrder(it)
                    },
                    setSortDirection = {
                        gamesViewModel.setSortDirection(it)
                    },
                )
                GamesList(
                    games = games,
                    remoteClientMode = mainViewModel.remoteClientMode,
                    sfwMode = sfwMode,
                    editGame = {
                        selectedGame = it
                    },
                    launchGame = {
                        gamesViewModel.launchGame(it)
                    },
                    togglePlaying = {
                        gamesViewModel.togglePlaying(it)
                    },
                    toggleCompleted = {
                        gamesViewModel.toggleCompleted(it)
                    },
                    toggleWaitingForUpdate = {
                        gamesViewModel.toggleWaitingForUpdate(it)
                    },
                    toggleHidden = {
                        gamesViewModel.toggleHidden(it)
                    },
                    resetUpdateAvailable = { availableVersion, game ->
                        gamesViewModel.resetUpdateAvailable(availableVersion, game)
                    },
                    updateRating = { rating, game ->
                        gamesViewModel.updateRating(rating, game)
                    },
                )
            }
        }
        selectedGame?.let {
            EditGameDialog(
                editGameViewModel = koinInject(parameters = { parametersOf(it) }),
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
                    gamesViewModel.launchGame(game, executablePath)
                },
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
        toastMessage?.let {
            Toast(
                text = it,
            )
        }
    }
}

@Composable
expect fun ToolbarActions(
    modifier: Modifier = Modifier,
    remoteClientMode: Boolean,
    sfwMode: Boolean,
    startUpdateCheck: () -> Unit,
    onImportGameClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onSfwModeClicked: () -> Unit,
    exitApplication: () -> Unit,
)

@Composable
private fun SortFilter(
    games: List<Game>,
    currentFilter: Filter,
    currentSortOrder: SortOrder,
    currentSortDirection: SortDirection,
    modifier: Modifier = Modifier,
    setFilter: (filter: Filter) -> Unit,
    setSortOrder: (sortOrder: SortOrder) -> Unit,
    setSortDirection: (sortDirection: SortDirection) -> Unit,
) {
    var showFilterDropdown by remember { mutableStateOf(false) }
    var showSortOrderDropdown by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
    ) {
        Row {
            Row(
                modifier = Modifier.clickable {
                    showFilterDropdown = true
                },
            ) {
                Text(R.strings.filterLabel)
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    buildString {
                        append(currentFilter.label)
                        append("(")
                        append(games.size)
                        append(")")
                    },
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Text("|")
            Spacer(modifier = Modifier.width(5.dp))
            Row(
                modifier = Modifier.clickable {
                    showSortOrderDropdown = true
                },
            ) {
                Text(R.strings.sortLabel)
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    buildString {
                        append(currentSortOrder.label)
                        append("(")
                        append(currentSortDirection.label)
                        append(")")
                    },
                )
            }
        }
        DropdownMenu(
            expanded = showFilterDropdown,
            onDismissRequest = {
                showFilterDropdown = false
            },
        ) {
            Filter.entries.forEach {
                DropdownMenuItem(
                    onClick = {
                        showFilterDropdown = false
                        setFilter(it)
                    },
                ) {
                    Text(it.label)
                }
            }
        }
        DropdownMenu(
            expanded = showSortOrderDropdown,
            onDismissRequest = {
                showSortOrderDropdown = false
            },
        ) {
            SortOrder.entries.forEach {
                DropdownMenuItem(
                    onClick = {
                        showSortOrderDropdown = false
                        if (currentSortOrder != it) {
                            setSortOrder(it)
                        } else {
                            when (currentSortDirection) {
                                SortDirection.Ascending -> setSortDirection(SortDirection.Descending)
                                SortDirection.Descending -> setSortDirection(SortDirection.Ascending)
                            }
                        }
                    },
                ) {
                    Text(it.label)
                }
            }
        }
    }
}

@Composable
private fun GamesList(
    games: List<Game>,
    remoteClientMode: Boolean,
    sfwMode: Boolean,
    editGame: (game: Game) -> Unit,
    launchGame: (game: Game) -> Unit,
    togglePlaying: (game: Game) -> Unit,
    toggleCompleted: (game: Game) -> Unit,
    toggleWaitingForUpdate: (game: Game) -> Unit,
    resetUpdateAvailable: (availableVersion: String, game: Game) -> Unit,
    toggleHidden: (game: Game) -> Unit,
    updateRating: (rating: Int, game: Game) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(gamesGridCellMinSizeDp()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(10.dp),
    ) {
        items(games) { game ->
            GameItem(
                game = game,
                remoteClientMode = remoteClientMode,
                sfwMode = sfwMode,
                editGame = editGame,
                launchGame = launchGame,
                togglePlaying = togglePlaying,
                toggleCompleted = toggleCompleted,
                toggleWaitingForUpdate = toggleWaitingForUpdate,
                resetUpdateAvailable = resetUpdateAvailable,
                toggleHidden = toggleHidden,
                updateRating = updateRating,
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun GameItem(
    game: Game,
    remoteClientMode: Boolean,
    sfwMode: Boolean,
    editGame: (game: Game) -> Unit,
    launchGame: (game: Game) -> Unit,
    togglePlaying: (game: Game) -> Unit,
    toggleCompleted: (game: Game) -> Unit,
    toggleWaitingForUpdate: (game: Game) -> Unit,
    resetUpdateAvailable: (availableVersion: String, game: Game) -> Unit,
    toggleHidden: (game: Game) -> Unit,
    updateRating: (rating: Int, game: Game) -> Unit,
) {
    val cardHoverInteractionSource = remember { MutableInteractionSource() }

    CompositionLocalProvider(
        LocalImageLoader provides koinInject(),
    ) {
        Card(
            modifier = Modifier
                .hoverable(cardHoverInteractionSource)
                .clickable {
                    if (!remoteClientMode) {
                        launchGame(game)
                    }
                },
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(bottom = 10.dp),
            ) {
                Image(
                    painter = rememberImagePainter(
                        request = ImageRequest {
                            data(game.imageUrl)
                            if (sfwMode) {
                                blur(30)
                            }
                        },
                    ),
                    contentDescription = null,
                    modifier = Modifier.aspectRatio(3.5f),
                    contentScale = ContentScale.Crop,
                )

                Row(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp).fillMaxWidth(),
                ) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Image(
                        painter = painterResource(R.images.playing),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            if (!remoteClientMode) {
                                togglePlaying(game)
                            }
                        },
                        colorFilter = ColorFilter.tint(
                            if (game.playState == PlayState.Playing) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                        ),
                    )
                    Image(
                        painter = painterResource(R.images.completed),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            if (!remoteClientMode) {
                                toggleCompleted(game)
                            }
                        },
                        colorFilter = ColorFilter.tint(
                            if (game.playState == PlayState.Completed) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                        ),
                    )
                    Image(
                        painter = painterResource(R.images.waiting),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            if (!remoteClientMode) {
                                toggleWaitingForUpdate(game)
                            }
                        },
                        colorFilter = ColorFilter.tint(
                            if (game.playState == PlayState.WaitingForUpdate) {
                                MaterialTheme.colors.primary
                            } else {
                                MaterialTheme.colors.onSurface
                            },
                        ),
                    )
                    if (game.updateAvailable) {
                        Image(
                            painter = painterResource(R.images.update),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                                if (!remoteClientMode) {
                                    game.availableVersion?.let {
                                        resetUpdateAvailable(it, game)
                                    }
                                }
                            },
                        )
                    }
                    if (game.executablePaths.isEmpty()) {
                        Image(
                            painter = painterResource(R.images.warning),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically),
                        )
                    }
                    if (!remoteClientMode) {
                        Image(
                            painter = painterResource(if (game.hidden) R.images.visible else R.images.gone),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                                toggleHidden(game)
                            },
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                        )
                    }
                    if (!remoteClientMode) {
                        Image(
                            painter = painterResource(R.images.edit),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                                editGame(game)
                            },
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
                ) {
                    InfoItem(R.strings.infoLabelPlayTime, formatPlayTime(game.playTime))
                    InfoItem(R.strings.infoLabelVersion, game.version)
                    InfoItem(R.strings.infoLabelAvailableVersion, game.availableVersion ?: R.strings.noValue)
                    InfoItem(
                        R.strings.infoLabelReleaseDate,
                        if (game.releaseDate <= 0L) R.strings.noValue else releaseDateFormat.format(game.releaseDate),
                    )
                    InfoItem(
                        R.strings.infoLabelFirstReleaseDate,
                        if (game.firstReleaseDate <= 0L) R.strings.noValue else releaseDateFormat.format(game.firstReleaseDate),
                    )
                }
                RatingBar(
                    rating = game.rating,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    onClick = { rating ->
                        if (!remoteClientMode) {
                            updateRating(rating, game)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String,
) {
    Row {
        Text(
            text = label,
            modifier = Modifier.padding(end = 10.dp),
            style = MaterialTheme.typography.subtitle1,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.subtitle1,
        )
    }
}

@Composable
expect fun RowScope.ToolbarTitle(content: @Composable RowScope.() -> Unit)

@Composable
expect fun RowScope.ToolbarSearch(content: @Composable RowScope.() -> Unit)

@Composable
expect fun RowScope.ToolbarState(content: @Composable RowScope.() -> Unit)

private fun State.buildText() =
    buildString {
        when (val state = this@buildText) {
            State.Idle -> append(R.strings.stateIdle)
            is State.Playing -> append(R.strings.statePlaying.format(state.game.title))
            State.Syncing -> append(R.strings.stateSyncing)
            State.UpdateCheckRunning -> append(R.strings.stateCheckingForUpdates)
        }
    }
