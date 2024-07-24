package org.skynetsoftware.avnlauncher.ui.screen.main.games

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.infoLabelFirstPlayed
import org.skynetsoftware.avnlauncher.app.generated.resources.infoLabelLastPlayed
import org.skynetsoftware.avnlauncher.app.generated.resources.infoLabelPlayTime
import org.skynetsoftware.avnlauncher.app.generated.resources.infoLabelReleaseDate
import org.skynetsoftware.avnlauncher.app.generated.resources.infoLabelVersion
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.ui.ext.lastPlayedDisplayValue
import org.skynetsoftware.avnlauncher.ui.ext.releaseDateDisplayValue
import org.skynetsoftware.avnlauncher.ui.ext.titleWithSfwFilterAndSearchMatchHighlight
import org.skynetsoftware.avnlauncher.ui.ext.versionDisplayValue
import org.skynetsoftware.avnlauncher.utils.formatPlayTime
import java.text.SimpleDateFormat

@Composable
fun GamesList(
    games: List<Game>,
    runningGame: Game?,
    sfwMode: Boolean,
    query: String?,
    dateFormat: SimpleDateFormat,
    timeFormat: SimpleDateFormat,
    gameDetails: (game: Game) -> Unit,
    launchGame: (game: Game) -> Unit,
    stopGame: () -> Unit,
    resetUpdateAvailable: (availableVersion: String, game: Game) -> Unit,
    updateRating: (rating: Int, game: Game) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(10.dp),
    ) {
        items(games) { game ->
            GameItem(
                game = game,
                runningGame = runningGame,
                sfwMode = sfwMode,
                query = query,
                dateFormat = dateFormat,
                timeFormat = timeFormat,
                gameDetails = gameDetails,
                launchGame = launchGame,
                stopGame = stopGame,
                resetUpdateAvailable = resetUpdateAvailable,
                updateRating = updateRating,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
private fun GameItem(
    game: Game,
    runningGame: Game?,
    sfwMode: Boolean,
    query: String?,
    dateFormat: SimpleDateFormat,
    timeFormat: SimpleDateFormat,
    gameDetails: (game: Game) -> Unit,
    launchGame: (game: Game) -> Unit,
    stopGame: () -> Unit,
    resetUpdateAvailable: (availableVersion: String, game: Game) -> Unit,
    updateRating: (rating: Int, game: Game) -> Unit,
) {
    GameItemBase(
        game = game,
        runningGame = runningGame,
        launchGame = launchGame,
        stopGame = stopGame,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier,
            ) {
                Text(
                    text = game.titleWithSfwFilterAndSearchMatchHighlight(sfwMode, query),
                    style = MaterialTheme.typography.h6.copy(fontSize = 18.sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier,
                )
                Spacer(
                    modifier = Modifier.width(10.dp),
                )
                Rating(
                    game = game,
                    updateRating = updateRating,
                    modifier = Modifier.weight(1f),
                )
                F95LinkIcon(
                    modifier = Modifier,
                    game = game,
                )
                UpdateAvailableIcon(
                    modifier = Modifier,
                    game = game,
                    resetUpdateAvailable = resetUpdateAvailable,
                )
                ExecutablePathMissingIcon(
                    modifier = Modifier,
                    game = game,
                )
                DetailsIcon(
                    game = game,
                    modifier = Modifier,
                    gameDetails = gameDetails,
                )
            }
            Row {
                InfoItem(
                    label = stringResource(Res.string.infoLabelPlayTime),
                    value = formatPlayTime(game.totalPlayTime),
                    labelStyle = MaterialTheme.typography.subtitle2,
                    valueStyle = MaterialTheme.typography.subtitle2,
                    maxLines = 1,
                )
                Text(" | ")
                InfoItem(
                    stringResource(Res.string.infoLabelFirstPlayed),
                    "${dateFormat.format(game.firstPlayedTime)} ${timeFormat.format(game.firstPlayedTime)}",
                    labelStyle = MaterialTheme.typography.subtitle2,
                    valueStyle = MaterialTheme.typography.subtitle2,
                    maxLines = 1,
                )
                Text(" | ")
                InfoItem(
                    stringResource(Res.string.infoLabelLastPlayed),
                    game.lastPlayedDisplayValue(dateFormat, timeFormat),
                    labelStyle = MaterialTheme.typography.subtitle2,
                    valueStyle = MaterialTheme.typography.subtitle2,
                    maxLines = 1,
                )
                Text(" | ")
                InfoItem(
                    stringResource(Res.string.infoLabelVersion),
                    game.versionDisplayValue(),
                    labelStyle = MaterialTheme.typography.subtitle2,
                    valueStyle = MaterialTheme.typography.subtitle2,
                    maxLines = 1,
                )
                Text(" | ")
                InfoItem(
                    stringResource(Res.string.infoLabelReleaseDate),
                    game.releaseDateDisplayValue(dateFormat),
                    labelStyle = MaterialTheme.typography.subtitle2,
                    valueStyle = MaterialTheme.typography.subtitle2,
                    maxLines = 1,
                )
            }
            game.notes?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.caption,
                    fontStyle = FontStyle.Italic,
                )
            }
            if (!query.isNullOrBlank()) {
                FlowRow {
                    game.tags.filter { it.lowercase().contains(query.lowercase()) }.forEach {
                        Chip(
                            modifier = Modifier.padding(horizontal = 2.dp),
                            onClick = {},
                        ) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.overline,
                            )
                        }
                    }
                }
            }
        }
    }
}
