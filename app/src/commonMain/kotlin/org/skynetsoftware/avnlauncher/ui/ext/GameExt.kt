package org.skynetsoftware.avnlauncher.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.noValue
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.utils.highlightRegions
import java.text.SimpleDateFormat
import kotlin.random.Random

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
                append(title.highlightRegions(query))
            } else {
                append(title)
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Game.releaseDateDisplayValue(dateFormat: SimpleDateFormat) =
    buildString {
        append(
            if (releaseDate <= 0L) {
                stringResource(Res.string.noValue)
            } else {
                dateFormat.format(releaseDate)
            },
        )
        if (firstReleaseDate > 0L) {
            append(" (")
            append(dateFormat.format(firstReleaseDate))
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

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Game.lastPlayedDisplayValue(
    dateFormat: SimpleDateFormat,
    timeFormat: SimpleDateFormat,
): String {
    return if (lastPlayedTime > 0L) {
        "${dateFormat.format(lastPlayedTime)} ${timeFormat.format(lastPlayedTime)}"
    } else {
        stringResource(Res.string.noValue)
    }
}
