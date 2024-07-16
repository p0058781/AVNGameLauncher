@file:Suppress("TooManyFunctions")

package org.skynetsoftware.avnlauncher.ui.screen.main.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.LocalWindowControl
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.edit
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationEditGame
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationExecutablePathMissing
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationF95Link
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationGameDetails
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationRating
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationUpdateAvailable
import org.skynetsoftware.avnlauncher.app.generated.resources.import
import org.skynetsoftware.avnlauncher.app.generated.resources.info
import org.skynetsoftware.avnlauncher.app.generated.resources.link
import org.skynetsoftware.avnlauncher.app.generated.resources.noGamesTextPart1
import org.skynetsoftware.avnlauncher.app.generated.resources.noGamesTextPart2
import org.skynetsoftware.avnlauncher.app.generated.resources.refresh
import org.skynetsoftware.avnlauncher.app.generated.resources.warning
import org.skynetsoftware.avnlauncher.data.f95.createF95ThreadUrl
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.GamesDisplayMode
import org.skynetsoftware.avnlauncher.domain.model.GridColumns
import org.skynetsoftware.avnlauncher.domain.model.isF95Game
import org.skynetsoftware.avnlauncher.link.ExternalLinkUtils
import org.skynetsoftware.avnlauncher.ui.component.HoverExplanation
import org.skynetsoftware.avnlauncher.ui.component.RatingBar
import org.skynetsoftware.avnlauncher.ui.theme.UpdateAvailable
import org.skynetsoftware.avnlauncher.ui.theme.Warning
import org.skynetsoftware.avnlauncher.utils.collectIsHoveredAsStateDelayed
import java.net.URI
import java.text.SimpleDateFormat

@Composable
fun Games(
    games: List<Game>,
    sfwMode: Boolean,
    query: String?,
    gamesDisplayMode: GamesDisplayMode,
    imageAspectRatio: Float,
    dateFormat: SimpleDateFormat,
    timeFormat: SimpleDateFormat,
    gridColumns: GridColumns,
    gameDetails: (game: Game) -> Unit,
    launchGame: (game: Game) -> Unit,
    resetUpdateAvailable: (availableVersion: String, game: Game) -> Unit,
    updateRating: (rating: Int, game: Game) -> Unit,
) {
    if (games.isEmpty()) {
        val modId = "importIcon"
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = buildAnnotatedString {
                    append(stringResource(Res.string.noGamesTextPart1))
                    append(" ")
                    appendInlineContent(modId, "[importIcon]")
                    append(" ")
                    append(stringResource(Res.string.noGamesTextPart2))
                },
                inlineContent = mapOf(
                    Pair(
                        modId,
                        InlineTextContent(
                            Placeholder(
                                width = 20.sp,
                                height = 20.sp,
                                placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
                            ),
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.import),
                                contentDescription = "",
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize(),
                            )
                        },
                    ),
                ),
                textAlign = TextAlign.Center,
            )
        }
    } else {
        when (gamesDisplayMode) {
            GamesDisplayMode.Grid -> GamesGrid(
                games = games,
                sfwMode = sfwMode,
                query = query,
                imageAspectRatio = imageAspectRatio,
                dateFormat = dateFormat,
                timeFormat = timeFormat,
                gridColumns = gridColumns,
                gameDetails = gameDetails,
                launchGame = launchGame,
                resetUpdateAvailable = resetUpdateAvailable,
                updateRating = updateRating,
            )

            GamesDisplayMode.List -> GamesList(
                games = games,
                sfwMode = sfwMode,
                query = query,
                dateFormat = dateFormat,
                timeFormat = timeFormat,
                gameDetails = gameDetails,
                launchGame = launchGame,
                resetUpdateAvailable = resetUpdateAvailable,
                updateRating = updateRating,
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun EditIcon(
    game: Game,
    modifier: Modifier = Modifier,
    editGame: (game: Game) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsStateDelayed()
    Image(
        painter = painterResource(Res.drawable.edit),
        contentDescription = null,
        modifier = modifier.size(30.dp).padding(5.dp).clickable {
            editGame(game)
        }.hoverable(interactionSource),
        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
    )
    if (isHovered && LocalWindowControl.current?.windowFocused?.value == true) {
        HoverExplanation(stringResource(Res.string.hoverExplanationEditGame))
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun DetailsIcon(
    game: Game,
    modifier: Modifier = Modifier,
    gameDetails: (game: Game) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsStateDelayed()
    Image(
        painter = painterResource(Res.drawable.info),
        contentDescription = null,
        modifier = modifier.size(30.dp).padding(5.dp).clickable {
            gameDetails(game)
        }.hoverable(interactionSource),
        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
    )
    if (isHovered && LocalWindowControl.current?.windowFocused?.value == true) {
        HoverExplanation(stringResource(Res.string.hoverExplanationGameDetails))
    }
}

@Composable
fun F95LinkIcon(
    modifier: Modifier = Modifier,
    game: Game,
    externalLinkUtils: ExternalLinkUtils = koinInject(),
) {
    if (game.isF95Game()) {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsStateDelayed()
        Image(
            painter = painterResource(Res.drawable.link),
            contentDescription = null,
            modifier = modifier.height(30.dp).padding(5.dp)
                .clickable {
                    externalLinkUtils.openInBrowser(
                        URI.create(game.f95ZoneThreadId.createF95ThreadUrl()),
                    )
                }.hoverable(interactionSource),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
        )
        if (isHovered && LocalWindowControl.current?.windowFocused?.value == true) {
            HoverExplanation(stringResource(Res.string.hoverExplanationF95Link))
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun UpdateAvailableIcon(
    modifier: Modifier = Modifier,
    game: Game,
    resetUpdateAvailable: (availableVersion: String, game: Game) -> Unit,
) {
    if (game.updateAvailable) {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsStateDelayed()
        Image(
            painter = painterResource(Res.drawable.refresh),
            colorFilter = ColorFilter.tint(UpdateAvailable),
            contentDescription = null,
            modifier = modifier.size(30.dp).padding(5.dp).clickable {
                game.availableVersion?.let {
                    resetUpdateAvailable(it, game)
                }
            }.hoverable(interactionSource),
        )
        if (isHovered && LocalWindowControl.current?.windowFocused?.value == true) {
            HoverExplanation(stringResource(Res.string.hoverExplanationUpdateAvailable))
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ExecutablePathMissingIcon(
    modifier: Modifier = Modifier,
    game: Game,
) {
    if (game.executablePaths.isEmpty()) {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsStateDelayed()
        Image(
            painter = painterResource(Res.drawable.warning),
            colorFilter = ColorFilter.tint(Warning),
            contentDescription = null,
            modifier = modifier.size(30.dp).padding(5.dp).hoverable(interactionSource),
        )
        if (isHovered && LocalWindowControl.current?.windowFocused?.value == true) {
            HoverExplanation(stringResource(Res.string.hoverExplanationExecutablePathMissing))
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Rating(
    modifier: Modifier = Modifier,
    game: Game,
    updateRating: (rating: Int, game: Game) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsStateDelayed()
    Row(
        modifier = modifier.hoverable(interactionSource),
    ) {
        RatingBar(
            rating = game.rating,
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = { rating ->
                updateRating(if (game.rating == rating) 0 else rating, game)
            },
        )
        if (game.isF95Game()) {
            Spacer(
                modifier = Modifier.width(10.dp),
            )
            Text(
                text = "(${game.f95Rating})",
                modifier = Modifier.align(Alignment.CenterVertically),
                style = MaterialTheme.typography.body2,
            )
        }
        if (isHovered && LocalWindowControl.current?.windowFocused?.value == true) {
            HoverExplanation(stringResource(Res.string.hoverExplanationRating))
        }
    }
}

@Composable
fun GameItemBase(
    game: Game,
    launchGame: (game: Game) -> Unit,
    content: @Composable () -> Unit,
) {
    val cardHoverInteractionSource = remember { MutableInteractionSource() }
    Card(
        modifier = Modifier
            .hoverable(cardHoverInteractionSource)
            .clickable {
                launchGame(game)
            },
        shape = RoundedCornerShape(10.dp),
    ) {
        content()
    }
}

@Composable
fun InfoItem(
    label: String,
    value: String,
    labelStyle: TextStyle = MaterialTheme.typography.subtitle1,
    valueStyle: TextStyle = MaterialTheme.typography.subtitle1,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Text(
            text = label,
            modifier = Modifier.padding(end = 10.dp),
            style = labelStyle,
            maxLines = maxLines,
        )
        Text(
            text = value,
            style = valueStyle,
            maxLines = maxLines,
        )
    }
}
