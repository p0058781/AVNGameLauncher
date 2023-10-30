@file:OptIn(ExperimentalResourceApi::class)

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
import org.skynetsoftware.avnlauncher.data.repository.Filter
import org.skynetsoftware.avnlauncher.data.repository.SortDirection
import org.skynetsoftware.avnlauncher.data.repository.SortOrder
import org.skynetsoftware.avnlauncher.data.UpdateChecker
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.model.PlayState
import org.skynetsoftware.avnlauncher.launcher.GameLauncher
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.ui.component.AutoSizeText
import org.skynetsoftware.avnlauncher.ui.component.RatingBar
import org.skynetsoftware.avnlauncher.ui.component.Toast
import org.skynetsoftware.avnlauncher.ui.screen.editgame.EditGameDialog
import org.skynetsoftware.avnlauncher.ui.theme.CardColor
import org.skynetsoftware.avnlauncher.ui.theme.CardHoverColor
import org.skynetsoftware.avnlauncher.ui.theme.materialColors
import org.skynetsoftware.avnlauncher.ui.viewmodel.GamesViewModel
import org.skynetsoftware.avnlauncher.utils.format
import org.skynetsoftware.avnlauncher.utils.formatPlayTime
import org.skynetsoftware.avnlauncher.utils.imageLoader

@Composable
fun MainScreen() {

    val updateChecker = koinInject<UpdateChecker>()
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    var importGameDialogVisible by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    MaterialTheme(
        colors = materialColors
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column {
                Actions(
                    showToast = {
                        toastMessage = it
                    },
                    onImportGameClicked = {
                        importGameDialogVisible = true
                    }
                )
                SortFilter()
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
                showToast = {
                    toastMessage = it
                }
            )
        }
        if(importGameDialogVisible) {
            ImportGameDialog(
                onCloseRequest = {
                    importGameDialogVisible = false
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
    onImportGameClicked: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Action(R.images.import) {
            onImportGameClicked()
        }
        Action(R.images.refresh) {
            updateChecker.startUpdateCheck(true) { updateResult ->
                showToast(updateResult.buildToastMessage())
            }
        }
    }

}

@Composable
fun Action(
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
private fun SortFilter() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        FilterItems(
            modifier = Modifier.weight(1f).fillMaxWidth()
        )
        SortOrderItems(
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun FilterItems(
    modifier: Modifier = Modifier,
    gamesViewModel: GamesViewModel = koinInject()
) {
    val games by remember { gamesViewModel.games }.collectAsState()
    Column(
        modifier = modifier.padding(start = 10.dp, end = 10.dp)
    ) {
        val currentFilter by remember { gamesViewModel.filter }.collectAsState()
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically).weight(1f).fillMaxWidth(),
                text = R.strings.filterTitle.format(games.size),
                style = MaterialTheme.typography.h5,
            )
        }
        LazyVerticalGrid(
            modifier = Modifier.padding(top = 10.dp),
            columns = GridCells.Adaptive(150.dp)
        ) {

            items(Filter.entries.size) {
                val filter = Filter.entries[it]
                val isChipSelected = currentFilter == filter
                Chip(
                    onClick = {
                        if (!isChipSelected) {
                            gamesViewModel.setFilter(filter)
                        }
                    },
                    modifier = Modifier.padding(end = 10.dp),
                    border = ChipDefaults.outlinedBorder,
                    colors = if (isChipSelected) ChipDefaults.chipColors() else ChipDefaults.outlinedChipColors(),
                ) {
                    AutoSizeText(
                        text = filter.label,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SortOrderItems(
    modifier: Modifier = Modifier,
    gamesViewModel: GamesViewModel = koinInject(),
) {
    Column(
        modifier = modifier.padding(start = 10.dp, end = 10.dp)
    ) {
        val currentSortOrder by remember { gamesViewModel.sortOrder }.collectAsState()
        val currentDirection by remember { gamesViewModel.sortDirection }.collectAsState()
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = R.strings.sortOrderTitle,
                style = MaterialTheme.typography.h5,
            )
        }
        LazyVerticalGrid(
            modifier = Modifier.padding(top = 10.dp),
            columns = GridCells.Adaptive(150.dp)
        ) {

            items(SortOrder.entries.size) {
                val sortOrder = SortOrder.entries[it]
                val isChipSelected = currentSortOrder == sortOrder
                Chip(
                    onClick = {
                        if (isChipSelected) {
                            when (currentDirection) {
                                SortDirection.Ascending -> gamesViewModel.setSortDirection(SortDirection.Descending)
                                SortDirection.Descending -> gamesViewModel.setSortDirection(SortDirection.Ascending)
                            }
                        } else {
                            gamesViewModel.setSortOrder(sortOrder)
                        }
                    },
                    modifier = Modifier.padding(end = 10.dp),
                    border = ChipDefaults.outlinedBorder,
                    colors = if (isChipSelected) ChipDefaults.chipColors() else ChipDefaults.outlinedChipColors(),
                ) {
                    AutoSizeText(
                        text = "${sortOrder.label}${if (isChipSelected) currentDirection.label else ""}",
                        modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp),
                        style = MaterialTheme.typography.h6,
                    )
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
        columns = GridCells.Adaptive((Toolkit.getDefaultToolkit().screenSize.width / 5).dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(games) { game ->
            GameItem(game, editGame = editGame)
        }
    }

}

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
        LocalImageLoader provides imageLoader(koinInject())
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
                    InfoItem(R.strings.infoLabelReleaseDate, game.releaseDate ?: R.strings.noValue)
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