package org.skynetsoftware.avnlauncher.ui.screen.main.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.rememberImagePainter
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.label
import org.skynetsoftware.avnlauncher.utils.formatPlayTime
import org.skynetsoftware.avnlauncher.utils.gamesGridCellMinSizeDp

private const val GAME_IMAGE_ASPECT_RATIO = 3.5f

@Composable
fun GamesGrid(
    games: List<Game>,
    sfwMode: Boolean,
    query: String?,
    editGame: (game: Game) -> Unit,
    launchGame: (game: Game) -> Unit,
    resetUpdateAvailable: (availableVersion: String, game: Game) -> Unit,
    updateRating: (rating: Int, game: Game) -> Unit,
    updateFavorite: (favorite: Boolean, game: Game) -> Unit,
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
                query = query,
                editGame = editGame,
                launchGame = launchGame,
                resetUpdateAvailable = resetUpdateAvailable,
                updateRating = updateRating,
                updateFavorite = updateFavorite,
            )
        }
    }
}

@Suppress("LongMethod", "CyclomaticComplexMethod")
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
private fun GameItem(
    game: Game,
    sfwMode: Boolean,
    query: String?,
    editGame: (game: Game) -> Unit,
    launchGame: (game: Game) -> Unit,
    resetUpdateAvailable: (availableVersion: String, game: Game) -> Unit,
    updateRating: (rating: Int, game: Game) -> Unit,
    updateFavorite: (favorite: Boolean, game: Game) -> Unit,
) {
    GameItemBase(
        game = game,
        launchGame = launchGame,
    ) {
        CompositionLocalProvider(
            LocalImageLoader provides koinInject(),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(bottom = 10.dp),
            ) {
                Box {
                    Image(
                        painter = rememberImagePainter(
                            request = ImageRequest {
                                if (sfwMode) {
                                    data("https://picsum.photos/seed/${game.f95ZoneThreadId}/400/200")
                                } else {
                                    data(game.customImageUrl ?: game.imageUrl)
                                }
                            },
                        ),
                        contentDescription = null,
                        modifier = Modifier.aspectRatio(GAME_IMAGE_ASPECT_RATIO),
                        contentScale = ContentScale.Crop,
                    )
                    Text(
                        modifier = Modifier.align(Alignment.BottomEnd)
                            .background(MaterialTheme.colors.surface).padding(5.dp).clip(
                                RoundedCornerShape(5.dp),
                            ),
                        text = game.playState.label(),
                        style = MaterialTheme.typography.body2,
                    )
                }

                Row(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp).fillMaxWidth(),
                ) {
                    Text(
                        text = game.titleWithSfwFilterAndSearchMatchHighlight(sfwMode, query),
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    EditIcon(
                        game = game,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        editGame = editGame,
                    )
                }

                Column(
                    modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
                ) {
                    InfoItem(stringResource(MR.strings.infoLabelPlayTime), formatPlayTime(game.playTime))
                    InfoItem(
                        stringResource(MR.strings.infoLabelFirstPlayed),
                        playedDateTimeFormat.format(game.firstPlayed),
                    )
                    InfoItem(
                        stringResource(MR.strings.infoLabelLastPlayed),
                        game.lastPlayedDisplayValue(),
                    )

                    InfoItem(stringResource(MR.strings.infoLabelVersion), game.versionDisplayValue())

                    InfoItem(
                        stringResource(MR.strings.infoLabelReleaseDate),
                        game.releaseDateDisplayValue(),
                    )

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

                    game.notes?.let {
                        Spacer(
                            modifier = Modifier.height(10.dp),
                        )
                        Text(
                            text = it,
                            style = MaterialTheme.typography.body2,
                            fontStyle = FontStyle.Italic,
                        )
                    }
                }
                Row(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                ) {
                    Rating(
                        modifier = Modifier.align(Alignment.CenterVertically).weight(1f),
                        game = game,
                        updateRating = updateRating,
                    )
                    AddToFavoritesIcon(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        game = game,
                        updateFavorite = updateFavorite,
                    )
                    F95LinkIcon(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        f95ZoneThreadId = game.f95ZoneThreadId,
                    )
                    UpdateAvailableIcon(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        game = game,
                        resetUpdateAvailable = resetUpdateAvailable,
                    )
                    ExecutablePathMissingIcon(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        game = game,
                    )
                }
            }
        }
    }
}
