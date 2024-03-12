package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.desktop.ui.tooling.preview.Preview
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.rememberAsyncImagePainter
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.data.model.Filter
import org.skynetsoftware.avnlauncher.data.model.SortDirection
import org.skynetsoftware.avnlauncher.data.model.SortOrder
import org.skynetsoftware.avnlauncher.data.UpdateChecker
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.launcher.GameLauncher
import org.skynetsoftware.avnlauncher.ui.component.AutoSizeText
import org.skynetsoftware.avnlauncher.ui.component.RatingBar
import org.skynetsoftware.avnlauncher.ui.component.Toast
import org.skynetsoftware.avnlauncher.ui.theme.CardColor
import org.skynetsoftware.avnlauncher.ui.theme.CardHoverColor
import org.skynetsoftware.avnlauncher.ui.theme.materialColors
import org.skynetsoftware.avnlauncher.utils.formatPlayTime
import org.skynetsoftware.avnlauncher.utils.imageLoader
import java.awt.Toolkit

@Composable
@Preview
fun MainScreen() {

    val gamesRepository: GamesRepository = koinInject()
    val updateChecker = koinInject<UpdateChecker>()
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    var importGameDialogVisible by remember { mutableStateOf<Boolean>(false) }
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
                selectedGame = it,
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
            appendLine("Update Available:")
            updates.forEach {
                appendLine(it.game.title)
            }
        } else {
            appendLine("No Updates Available")
        }
        val exceptions = this@buildToastMessage.filter { it.exception != null }
        if (exceptions.isNotEmpty()) {
            appendLine("Exceptions: ")
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
        Action("import.png") {
            onImportGameClicked()
        }
        Action("refresh.png") {
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
    gamesRepository: GamesRepository = koinInject()
) {
    val games by remember { gamesRepository.games }.collectAsState()
    Column(
        modifier = modifier.padding(start = 10.dp, end = 10.dp)
    ) {
        val currentFilter by remember { gamesRepository.filter }.collectAsState()
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically).weight(1f).fillMaxWidth(),
                text = "Filter (${games.size})",
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
                            gamesRepository.setFilter(filter)
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
    gamesRepository: GamesRepository = koinInject(),
) {
    Column(
        modifier = modifier.padding(start = 10.dp, end = 10.dp)
    ) {
        val currentSortOrder by remember { gamesRepository.sortOrder }.collectAsState()
        val currentDirection by remember { gamesRepository.sortDirection }.collectAsState()
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Sort Order",
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
                                SortDirection.Ascending -> gamesRepository.setSortDirection(SortDirection.Descending)
                                SortDirection.Descending -> gamesRepository.setSortDirection(SortDirection.Ascending)
                            }
                        } else {
                            gamesRepository.setSortOrder(sortOrder)
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
    gamesRepository: GamesRepository = koinInject(),
    editGame: (game: Game) -> Unit,
) {

    val games by remember { gamesRepository.games }.collectAsState()

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
    gamesRepository: GamesRepository = koinInject(),
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
                        painter = painterResource("playing.png"),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            gamesRepository.setPlaying(!game.playing, game.id)
                        },
                        colorFilter = ColorFilter.tint(if (game.playing) Color.Green else Color.White)
                    )
                    Image(
                        painter = painterResource("completed.png"),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            gamesRepository.setCompleted(!game.completed, game.id)
                        },
                        colorFilter = ColorFilter.tint(if (game.completed) Color.Green else Color.White)
                    )
                    Image(
                        painter = painterResource("waiting.png"),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            gamesRepository.setWaitingForUpdate(!game.waitingForUpdate, game.id)
                        },
                        colorFilter = ColorFilter.tint(if (game.waitingForUpdate) Color.Green else Color.White)
                    )
                    if (game.updateAvailable) {
                        Image(
                            painter = painterResource("update.png"),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                                game.availableVersion?.let {
                                    gamesRepository.updateUpdateAvailable(false, game.id)
                                    gamesRepository.updateVersion(it, game.id)
                                    gamesRepository.updateAvailableVersion(null, game.id)
                                }
                            }
                        )
                    }
                    if (game.executablePath.isBlank()) {
                        Image(
                            painter = painterResource("warning.png"),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically)
                        )
                    }
                    Image(
                        painter = painterResource(if (game.hidden) "visible.png" else "gone.png"),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(5.dp).align(Alignment.CenterVertically).clickable {
                            gamesRepository.updateHidden(!game.hidden, game.id)
                        },
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Image(
                        painter = painterResource("edit.png"),
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
                    InfoItem("Play Time:", formatPlayTime(game.playTime))
                    InfoItem("Version:", game.version)
                    InfoItem("Available Version:", game.availableVersion ?: "-")
                    InfoItem("Release Date:", game.releaseDate ?: "-")
                }
                RatingBar(
                    rating = game.rating ?: 0,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    onClick = { rating ->
                        gamesRepository.updateRating(rating, game.id)
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