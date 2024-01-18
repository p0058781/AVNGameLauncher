package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.rememberImagePainter
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.skynetsoftware.avnlauncher.LocalDraggableArea
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlayState
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.state.State
import org.skynetsoftware.avnlauncher.ui.component.RatingBar
import org.skynetsoftware.avnlauncher.ui.component.Toast
import org.skynetsoftware.avnlauncher.ui.screen.editgame.EditGameDialog
import org.skynetsoftware.avnlauncher.ui.screen.import.ImportGameDialog
import org.skynetsoftware.avnlauncher.ui.screen.settings.SettingsDialog
import org.skynetsoftware.avnlauncher.ui.viewmodel.GamesScreenModel
import org.skynetsoftware.avnlauncher.ui.viewmodel.MainScreenModel
import org.skynetsoftware.avnlauncher.updatechecker.UpdateCheckResult
import org.skynetsoftware.avnlauncher.utils.SimpleDateFormat
import org.skynetsoftware.avnlauncher.utils.collectAsMutableState
import org.skynetsoftware.avnlauncher.utils.formatPlayTime
import org.skynetsoftware.avnlauncher.utils.gamesGridCellMinSizeDp
import kotlin.random.Random

private val releaseDateFormat = SimpleDateFormat("yyyy-MM-dd")

data class MainScreen(
    val exitApplication: () -> Unit,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val mainScreenModel: MainScreenModel = navigator.getNavigatorScreenModel()
        val gamesScreenModel: GamesScreenModel = getScreenModel()
        val games by remember { gamesScreenModel.games }.collectAsState()
        val currentFilter by remember { gamesScreenModel.filter }.collectAsState()
        val currentSortOrder by remember { gamesScreenModel.sortOrder }.collectAsState()
        val currentSortDirection by remember { gamesScreenModel.sortDirection }.collectAsState()
        val sfwMode by remember { mainScreenModel.sfwMode }.collectAsState()

        var selectedGame by remember { mutableStateOf<Game?>(null) }
        val toastMessage by remember { mainScreenModel.toastMessage }.collectAsState()
        var showExecutablePathPicker by gamesScreenModel.showExecutablePathPicker.collectAsMutableState()

        val totalPlayTime by remember { gamesScreenModel.totalPlayTime }.collectAsState()
        val averagePlayTime by remember { gamesScreenModel.averagePlayTime }.collectAsState()
        var searchQuery by remember { gamesScreenModel.searchQuery }.collectAsMutableState(context = Dispatchers.Main.immediate)
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
                            OutlinedTextField(
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
                    gamesScreenModel.setFilter(it)
                },
                setSortOrder = {
                    gamesScreenModel.setSortOrder(it)
                },
                setSortDirection = {
                    gamesScreenModel.setSortDirection(it)
                },
            )
            GamesList(
                games = games,
                sfwMode = sfwMode,
                editGame = {
                    selectedGame = it
                },
                launchGame = {
                    gamesScreenModel.launchGame(it)
                },
                togglePlaying = {
                    gamesScreenModel.togglePlaying(it)
                },
                toggleCompleted = {
                    gamesScreenModel.toggleCompleted(it)
                },
                toggleWaitingForUpdate = {
                    gamesScreenModel.toggleWaitingForUpdate(it)
                },
                toggleHidden = {
                    gamesScreenModel.toggleHidden(it)
                },
                resetUpdateAvailable = { availableVersion, game ->
                    gamesScreenModel.resetUpdateAvailable(availableVersion, game)
                },
                updateRating = { rating, game ->
                    gamesScreenModel.updateRating(rating, game)
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
expect fun ToolbarActions(
    modifier: Modifier = Modifier,
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
                Text(stringResource(MR.strings.filterLabel))
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
                Text(stringResource(MR.strings.sortLabel))
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
                    launchGame(game)
                },
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(bottom = 10.dp),
            ) {
                val modifier = Modifier
                Image(
                    painter = rememberImagePainter(
                        request = ImageRequest {
                            if (sfwMode) {
                                data("https://picsum.photos/seed/${game.f95ZoneThreadId}/400/200")
                            } else {
                                data(game.imageUrl)
                            }
                        },
                    ),
                    contentDescription = null,
                    modifier = modifier.aspectRatio(3.5f),
                    contentScale = ContentScale.Crop,
                )

                Row(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp).fillMaxWidth(),
                ) {
                    Text(
                        text = game.sfwFilterTitle(sfwMode),
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Image(
                        painter = painterResource(R.images.playing),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            togglePlaying(game)
                        },
                        colorFilter = ColorFilter.tint(
                            if (game.playState == PlayState.Playing) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                        ),
                    )
                    Image(
                        painter = painterResource(R.images.completed),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            toggleCompleted(game)
                        },
                        colorFilter = ColorFilter.tint(
                            if (game.playState == PlayState.Completed) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                        ),
                    )
                    Image(
                        painter = painterResource(R.images.waiting),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            toggleWaitingForUpdate(game)
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
                                game.availableVersion?.let {
                                    resetUpdateAvailable(it, game)
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
                    Image(
                        painter = painterResource(if (game.hidden) R.images.visible else R.images.gone),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            toggleHidden(game)
                        },
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                    )

                    Image(
                        painter = painterResource(R.images.edit),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            editGame(game)
                        },
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                    )
                }

                Column(
                    modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
                ) {
                    InfoItem(stringResource(MR.strings.infoLabelPlayTime), formatPlayTime(game.playTime))
                    InfoItem(stringResource(MR.strings.infoLabelVersion), game.version)
                    InfoItem(
                        stringResource(MR.strings.infoLabelAvailableVersion),
                        game.availableVersion ?: stringResource(MR.strings.noValue),
                    )
                    InfoItem(
                        stringResource(MR.strings.infoLabelReleaseDate),
                        if (game.releaseDate <= 0L) {
                            stringResource(MR.strings.noValue)
                        } else {
                            releaseDateFormat.format(
                                game.releaseDate,
                            )
                        },
                    )
                    InfoItem(
                        stringResource(MR.strings.infoLabelFirstReleaseDate),
                        if (game.firstReleaseDate <= 0L) {
                            stringResource(
                                MR.strings.noValue,
                            )
                        } else {
                            releaseDateFormat.format(game.firstReleaseDate)
                        },
                    )
                }
                RatingBar(
                    rating = game.rating,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    onClick = { rating ->
                        updateRating(rating, game)
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

@Composable
private fun State.buildText() =
    buildString {
        when (val state = this@buildText) {
            State.Idle -> append(stringResource(MR.strings.stateIdle))
            is State.Playing -> append(stringResource(MR.strings.statePlaying, state.game.title))
            State.Syncing -> append(stringResource(MR.strings.stateSyncing))
            State.UpdateCheckRunning -> append(stringResource(MR.strings.stateCheckingForUpdates))
        }
    }

@Composable
fun UpdateCheckResult.buildToastMessage(): String {
    return buildString {
        val updates = this@buildToastMessage.games.filter { it.updateAvailable }
        if (updates.isNotEmpty()) {
            appendLine(
                stringResource(MR.strings.toastUpdateAvailable),
            )
            updates.forEach {
                appendLine(it.game.title)
            }
        } else {
            appendLine(stringResource(MR.strings.toastNoUpdatesAvailable))
        }
        val exceptions = this@buildToastMessage.games.filter { it.exception != null }
        if (exceptions.isNotEmpty()) {
            appendLine(stringResource(MR.strings.toastException))
            exceptions.forEach {
                append(it.game.title)
                append(": ")
                append(it.exception?.message)
            }
        }
    }
}

private val randomPhrases = arrayOf(
    "Wouldn't Harm a Fly",
    "A Cold Fish",
    "Wake Up Call",
    "Between a Rock and a Hard Place",
    "Playing Possum",
    "Top Drawer",
    "Flea Market",
    "Don't Look a Gift Horse In The Mouth",
    "A Day Late and a Dollar Short",
    "Cup Of Joe",
    "Break The Ice",
    "Eat My Hat",
    "Barking Up The Wrong Tree",
    "Short End of the Stick",
    "Under the Weather",
    "Right Out of the Gate",
    "Drawing a Blank",
    "Scot-free",
    "Jaws of Death",
    "Fish Out Of Water",
    "Quick On the Draw",
    "Go For Broke",
    "Hands Down",
    "No-Brainer",
    "Playing For Keeps",
    "Elephant in the Room",
    "Cry Over Spilt Milk",
    "What Goes Up Must Come Down",
    "Mouth-watering",
    "A Hair’s Breadth",
    "Money Doesn't Grow On Trees",
    "Up In Arms",
    "All Greek To Me",
    "A Dime a Dozen",
    "Burst Your Bubble",
    "Tough It Out",
    "Ugly Duckling",
    "Under Your Nose",
    "Not the Sharpest Tool in the Shed",
    "A Busy Bee",
    "Quick and Dirty",
    "Foaming At The Mouth",
    "Fit as a Fiddle",
    "Tug of War",
    "Plot Thickens - The",
    "Everything But The Kitchen Sink",
    "A Dog in the Manger",
    "Keep Your Eyes Peeled",
    "Man of Few Words",
    "A Cut Below",
    "A Lemon",
)

private fun Game.sfwFilterTitle(enabled: Boolean): String {
    return if (enabled) randomPhrases.random(Random(f95ZoneThreadId)) else title
}
