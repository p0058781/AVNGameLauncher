package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.rememberAsyncImagePainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.data.UpdateChecker
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.model.PlayState
import org.skynetsoftware.avnlauncher.data.repository.Filter
import org.skynetsoftware.avnlauncher.data.repository.SortDirection
import org.skynetsoftware.avnlauncher.data.repository.SortOrder
import org.skynetsoftware.avnlauncher.launcher.GameLauncher
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.ui.component.RatingBar
import org.skynetsoftware.avnlauncher.ui.component.Toast
import org.skynetsoftware.avnlauncher.ui.screen.editgame.EditGameDialog
import org.skynetsoftware.avnlauncher.ui.screen.import.ImportGameDialog
import org.skynetsoftware.avnlauncher.ui.screen.import.ImportGamesDialog
import org.skynetsoftware.avnlauncher.ui.theme.CardColor
import org.skynetsoftware.avnlauncher.ui.theme.CardHoverColor
import org.skynetsoftware.avnlauncher.ui.theme.materialColors
import org.skynetsoftware.avnlauncher.ui.viewmodel.GamesViewModel
import org.skynetsoftware.avnlauncher.utils.SimpleDateFormat
import org.skynetsoftware.avnlauncher.utils.formatPlayTime
import org.skynetsoftware.avnlauncher.utils.gamesGridCellMinSizeDp

private val releaseDateFormat = SimpleDateFormat("yyyy-MM-dd")

@Composable
fun MainScreen(exitApplication: () -> Unit) {
    val updateChecker = koinInject<UpdateChecker>()
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    var importGameDialogVisible by remember { mutableStateOf(false) }
    var importGamesDialogVisible by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    MaterialTheme(
        colors = materialColors
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
//TODO toolbar with options
            //TODO search
            Column {
                TopAppBar {
                    Actions(
                        showToast = {
                            toastMessage = it
                        },
                        onImportGameClicked = {
                            importGameDialogVisible = true
                        },
                        onImportGamesClicked = {
                            importGamesDialogVisible = true
                        },
                        exitApplication = exitApplication
                    )
                }
                SortFilter(
                    modifier = Modifier.align(Alignment.End).padding(10.dp)
                )
                GamesList(
                    editGame = {
                        selectedGame = it
                    }
                )
            }
        }
        selectedGame?.let {
            EditGameDialog(
                _selectedGame = it,
                onCloseRequest = {
                    selectedGame = null
                },
                showToast = { message ->
                    toastMessage = message
                }
            )
        }
        if (importGameDialogVisible) {
            ImportGameDialog(
                onCloseRequest = {
                    importGameDialogVisible = false
                },
                showToast = {
                    toastMessage = it
                }
            )
        }
        if (importGamesDialogVisible) {
            ImportGamesDialog(
                onCloseRequest = {
                    importGamesDialogVisible = false
                },
                showToast = {
                    toastMessage = it
                }
            )
        }
        toastMessage?.let {
            Toast(
                text = it,
                onDismiss = {
                    toastMessage = null
                }
            )
        }

    }
    updateChecker.startUpdateCheck(
        onComplete = { updateResult ->
            toastMessage = updateResult.buildToastMessage()
        }
    )

}

//TODO this needs to be moved somewhere lese, it does not belong here
private fun List<UpdateChecker.UpdateResult>.buildToastMessage(): String {
    return buildString {
        val updates = this@buildToastMessage.filter { it.updateAvailable }
        if (updates.isNotEmpty()) {
            appendLine(R.strings.toastUpdateAvailable)
            updates.forEach {
                appendLine(it.game.title)
            }
        } else {
            appendLine(R.strings.toastNoUpdatesAvailable)
        }
        val exceptions = this@buildToastMessage.filter { it.exception != null }
        if (exceptions.isNotEmpty()) {
            appendLine(R.strings.toastException)
            exceptions.forEach {
                append(it.game.title)
                append(": ")
                append(it.exception?.message)
            }
        }
    }
}

@Composable
private fun Actions(
    updateChecker: UpdateChecker = koinInject(),
    showToast: (text: String) -> Unit,
    onImportGameClicked: () -> Unit,
    onImportGamesClicked: () -> Unit,
    exitApplication: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Action(R.images.import) {
            onImportGameClicked()
        }
        //TODO remove import games
        Action(R.images.import) {
            onImportGamesClicked()
        }
        Action(R.images.refresh) {
            updateChecker.startUpdateCheck(true) { updateResult ->
                showToast(updateResult.buildToastMessage())
            }
        }
        Action(R.images.close) {
            exitApplication()
        }
    }

}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun Action(
    icon: String,
    action: () -> Unit
) {
    Box(
        modifier = Modifier.padding(10.dp)
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp).clickable {
                action()
            },
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}

@Composable
private fun SortFilter(
    gamesViewModel: GamesViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val games by remember { gamesViewModel.games }.collectAsState()
    val currentFilter by remember { gamesViewModel.filter }.collectAsState()
    val currentSortOrder by remember { gamesViewModel.sortOrder }.collectAsState()
    val currentSortDirection by remember { gamesViewModel.sortDirection }.collectAsState()

    var showFilterDropdown by remember { mutableStateOf(false) }
    var showSortOrderDropdown by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        Row {
            Row(
                modifier = Modifier.clickable {
                    showFilterDropdown = true
                }
            ) {
                Text(R.strings.filterLabel)
                Spacer(modifier = Modifier.width(5.dp))
                Text(buildString {
                    append(currentFilter.label)
                    append("(")
                    append(games.size)
                    append(")")
                })
            }
            Spacer(modifier = Modifier.width(5.dp))
            Text("|")
            Spacer(modifier = Modifier.width(5.dp))
            Row(
                modifier = Modifier.clickable {
                    showSortOrderDropdown = true
                }
            ) {
                Text(R.strings.sortLabel)
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    buildString {
                        append(currentSortOrder.label)
                        append("(")
                        append(currentSortDirection.label)
                        append(")")
                    }
                )
            }
        }
        DropdownMenu(
            expanded = showFilterDropdown,
            onDismissRequest = {
                showFilterDropdown = false
            }

        ) {
            Filter.entries.forEach {
                DropdownMenuItem(
                    onClick = {
                        showFilterDropdown = false
                        gamesViewModel.setFilter(it)
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
            }

        ) {
            SortOrder.entries.forEach {
                DropdownMenuItem(
                    onClick = {
                        showSortOrderDropdown = false
                        if(currentSortOrder != it) {
                            gamesViewModel.setSortOrder(it)
                        } else {
                            when(currentSortDirection) {
                                SortDirection.Ascending -> gamesViewModel.setSortDirection(SortDirection.Descending)
                                SortDirection.Descending -> gamesViewModel.setSortDirection(SortDirection.Ascending)
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
    gamesViewModem: GamesViewModel = koinInject(),
    editGame: (game: Game) -> Unit,
) {

    val games by remember { gamesViewModem.games }.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(gamesGridCellMinSizeDp()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(games) { game ->
            GameItem(game, editGame = editGame)
        }
    }

}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun GameItem(
    game: Game,
    gameLauncher: GameLauncher = koinInject(),
    gamesViewModel: GamesViewModel = koinInject(),
    editGame: (game: Game) -> Unit
) {
    val cardHoverInteractionSource = remember { MutableInteractionSource() }
    val cardHover by cardHoverInteractionSource.collectIsHoveredAsState()

    CompositionLocalProvider(
        LocalImageLoader provides koinInject()
    ) {
        Card(
            backgroundColor = if (cardHover) CardHoverColor else CardColor,
            modifier = Modifier
                .hoverable(cardHoverInteractionSource)
                .clickable {
                    gameLauncher.launch(game)
                },
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(bottom = 10.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        url = game.imageUrl
                    ),
                    contentDescription = null,
                    modifier = Modifier.aspectRatio(3.5f),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp).fillMaxWidth(),
                ) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Image(
                        painter = painterResource(R.images.playing),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            gamesViewModel.togglePlaying(game)
                        },
                        colorFilter = ColorFilter.tint(if (game.playState == PlayState.Playing) Color.Green else Color.White)
                    )
                    Image(
                        painter = painterResource(R.images.completed),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            gamesViewModel.toggleCompleted(game)
                        },
                        colorFilter = ColorFilter.tint(if (game.playState == PlayState.Completed) Color.Green else Color.White)
                    )
                    Image(
                        painter = painterResource(R.images.waiting),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            gamesViewModel.toggleWaitingForUpdate(game)
                        },
                        colorFilter = ColorFilter.tint(if (game.playState == PlayState.WaitingForUpdate) Color.Green else Color.White)
                    )
                    if (game.updateAvailable) {
                        Image(
                            painter = painterResource(R.images.update),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                                game.availableVersion?.let {
                                    gamesViewModel.resetUpdateAvailable(it, game)
                                }
                            }
                        )
                    }
                    if (game.executablePath.isNullOrBlank()) {
                        Image(
                            painter = painterResource(R.images.warning),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically)
                        )
                    }
                    Image(
                        painter = painterResource(if (game.hidden) R.images.visible else R.images.gone),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            gamesViewModel.toggleHidden(game)
                        },
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Image(
                        painter = painterResource(R.images.edit),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            editGame(game)
                        },
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }

                Column(
                    modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth()
                ) {
                    InfoItem(R.strings.infoLabelPlayTime, formatPlayTime(game.playTime))
                    InfoItem(R.strings.infoLabelVersion, game.version)
                    InfoItem(R.strings.infoLabelAvailableVersion, game.availableVersion ?: R.strings.noValue)
                    InfoItem(
                        R.strings.infoLabelReleaseDate,
                        if (game.releaseDate <= 0L) R.strings.noValue else releaseDateFormat.format(game.releaseDate)
                    )
                }
                RatingBar(
                    rating = game.rating,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    onClick = { rating ->
                        gamesViewModel.updateRating(rating, game)
                    }
                )

            }


        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Row {
        Text(
            text = label,
            modifier = Modifier.padding(end = 10.dp),
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            text = value,
            style = MaterialTheme.typography.subtitle1
        )
    }
}