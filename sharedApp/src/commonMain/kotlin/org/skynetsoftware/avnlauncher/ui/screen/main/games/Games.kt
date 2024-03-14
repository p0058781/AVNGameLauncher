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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.data.f95.createF95ThreadUrl
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.GamesDisplayMode
import org.skynetsoftware.avnlauncher.link.ExternalLinkUtils
import org.skynetsoftware.avnlauncher.resources.Images
import org.skynetsoftware.avnlauncher.ui.component.RatingBar
import org.skynetsoftware.avnlauncher.utils.SimpleDateFormat
import java.net.URI
import java.util.regex.Pattern
import kotlin.random.Random

val releaseDateFormat = SimpleDateFormat("MMM dd, yyyy")
val playedDateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm")

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Games(
    games: List<Game>,
    sfwMode: Boolean,
    query: String?,
    gamesDisplayMode: GamesDisplayMode,
    editGame: (game: Game) -> Unit,
    launchGame: (game: Game) -> Unit,
    resetUpdateAvailable: (availableVersion: String, game: Game) -> Unit,
    updateRating: (rating: Int, game: Game) -> Unit,
    updateFavorite: (favorite: Boolean, game: Game) -> Unit,
) {
    if (games.isEmpty()) {
        val modId = "importIcon"
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = buildAnnotatedString {
                    append(stringResource(MR.strings.noGamesTextPart1))
                    append(" ")
                    appendInlineContent(modId, "[importIcon]")
                    append(" ")
                    append(stringResource(MR.strings.noGamesTextPart2))
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
                                painter = painterResource(Images.import),
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
                editGame = editGame,
                launchGame = launchGame,
                resetUpdateAvailable = resetUpdateAvailable,
                updateRating = updateRating,
                updateFavorite = updateFavorite,
            )

            GamesDisplayMode.List -> GamesList(
                games = games,
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

@OptIn(ExperimentalResourceApi::class)
@Composable
fun EditIcon(
    game: Game,
    modifier: Modifier = Modifier,
    editGame: (game: Game) -> Unit,
) {
    Image(
        painter = painterResource(Images.edit),
        contentDescription = null,
        modifier = modifier.size(30.dp).padding(5.dp).clickable {
            editGame(game)
        },
        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AddToFavoritesIcon(
    modifier: Modifier = Modifier,
    game: Game,
    updateFavorite: (favorite: Boolean, game: Game) -> Unit,
) {
    Image(
        painter = painterResource(if (game.favorite) Images.heart_filled else Images.heart),
        contentDescription = null,
        modifier = modifier.height(30.dp).padding(5.dp)
            .clickable {
                updateFavorite(!game.favorite, game)
            },
        colorFilter = ColorFilter.tint(Color.Red),
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun F95LinkIcon(
    modifier: Modifier = Modifier,
    f95ZoneThreadId: Int,
    externalLinkUtils: ExternalLinkUtils = koinInject(),
) {
    if (f95ZoneThreadId > 0) {
        Image(
            painter = painterResource(Images.link),
            contentDescription = null,
            modifier = modifier.height(30.dp).padding(5.dp)
                .clickable {
                    externalLinkUtils.openInBrowser(
                        URI.create(f95ZoneThreadId.createF95ThreadUrl()),
                    )
                },
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
        )
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
        Image(
            painter = painterResource(Images.update),
            contentDescription = null,
            modifier = modifier.size(30.dp).padding(5.dp).clickable {
                game.availableVersion?.let {
                    resetUpdateAvailable(it, game)
                }
            },
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ExecutablePathMissingIcon(
    modifier: Modifier = Modifier,
    game: Game,
) {
    if (game.executablePaths.isEmpty()) {
        Image(
            painter = painterResource(Images.warning),
            contentDescription = null,
            modifier = modifier.size(30.dp).padding(5.dp),
        )
    }
}

@Composable
fun Rating(
    modifier: Modifier = Modifier,
    game: Game,
    updateRating: (rating: Int, game: Game) -> Unit,
) {
    Row(
        modifier = modifier,
    ) {
        RatingBar(
            rating = game.rating,
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = { rating ->
                updateRating(rating, game)
            },
        )
        Spacer(
            modifier = Modifier.width(10.dp),
        )
        Text(
            text = "(${game.f95Rating})",
            modifier = Modifier.align(Alignment.CenterVertically),
            style = MaterialTheme.typography.body2,
        )
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

fun Game.titleWithSfwFilterAndSearchMatchHighlight(
    sfwMode: Boolean,
    query: String?,
): AnnotatedString {
    return buildAnnotatedString {
        if (sfwMode) {
            append(randomPhrases.random(Random(f95ZoneThreadId)))
        } else {
            if (!query.isNullOrBlank()) {
                val highlightRanges = Regex(Pattern.quote(query.lowercase()))
                    .findAll(title.lowercase())
                    .map { it.range.first to it.range.first + query.length }.toList()
                if (highlightRanges.isEmpty()) {
                    append(title)
                } else {
                    var start = 0
                    var highlightRangeIndex = 0
                    while (start < title.length && highlightRangeIndex < highlightRanges.size) {
                        val highlightRange = highlightRanges[highlightRangeIndex]
                        append(title.substring(start, highlightRange.first))
                        withStyle(SpanStyle(color = Color.Yellow)) {
                            append(title.substring(highlightRange.first, highlightRange.second))
                        }
                        start = highlightRange.second
                        highlightRangeIndex++
                    }
                    if (start < title.length) {
                        append(title.substring(start, title.length))
                    }
                }
            } else {
                append(title)
            }
        }
    }
}

@Composable
fun Game.releaseDateDisplayValue() =
    buildString {
        append(
            if (releaseDate <= 0L) {
                stringResource(MR.strings.noValue)
            } else {
                releaseDateFormat.format(
                    releaseDate,
                )
            },
        )
        if (firstReleaseDate > 0L) {
            append(" (")
            append(releaseDateFormat.format(firstReleaseDate))
            append(")")
        }
    }

fun Game.versionDisplayValue() =
    buildString {
        append(version)
        if (availableVersion.isNullOrBlank().not()) {
            append(" (")
            append(availableVersion)
            append(")")
        }
    }

@Composable
fun Game.lastPlayedDisplayValue(): String {
    return if (lastPlayed > 0L) {
        playedDateTimeFormat.format(lastPlayed)
    } else {
        stringResource(MR.strings.noValue)
    }
}
