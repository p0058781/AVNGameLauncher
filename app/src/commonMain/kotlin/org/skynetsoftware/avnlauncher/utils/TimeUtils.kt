package org.skynetsoftware.avnlauncher.utils

import androidx.compose.runtime.Composable
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.nHours
import org.skynetsoftware.avnlauncher.app.generated.resources.nMinutes
import org.skynetsoftware.avnlauncher.app.generated.resources.nSeconds
import org.skynetsoftware.avnlauncher.app.generated.resources.noValue

private const val ONE_SECOND_MILLIS = 1000L
private const val ONE_HOUR_SECONDS = 3600L
private const val ONE_MINUTE_SECONDS = 60L
private const val HOURS_IN_DAY = 24f
private const val ONE_DAY_MS = 86400000f

@OptIn(ExperimentalResourceApi::class)
@Composable
fun formatPlayTime(playTime: Long?): String {
    if (playTime == null || playTime == 0L) {
        return stringResource(Res.string.noValue)
    }
    val totalSeconds = playTime / ONE_SECOND_MILLIS

    // Calculate hours, minutes, and seconds
    val hours = totalSeconds / ONE_HOUR_SECONDS
    val minutes = (totalSeconds % ONE_HOUR_SECONDS) / ONE_MINUTE_SECONDS
    val seconds = totalSeconds % ONE_MINUTE_SECONDS

    // Format the result
    return when {
        hours > 0 -> stringResource(Res.string.nHours, hours)
        minutes > 0 -> stringResource(Res.string.nMinutes, minutes)
        else -> stringResource(Res.string.nSeconds, seconds)
    }
}

fun Int?.hoursToMilliseconds(): Long? {
    this ?: return null
    return this * ONE_HOUR_SECONDS * ONE_SECOND_MILLIS
}

fun calculateAveragePlayTime(
    firstPlayedTime: Long,
    totalPlayTime: Long,
): Float {
    val now = Clock.System.now().toEpochMilliseconds()
    val totalTimeDays = ((now - firstPlayedTime) / ONE_DAY_MS).let { if (it < 1f) 1f else it }
    val totalPlayTimeDays = totalPlayTime / ONE_DAY_MS
    val dailyPlayTimeHours = (totalPlayTimeDays / totalTimeDays) * HOURS_IN_DAY
    return if (dailyPlayTimeHours > HOURS_IN_DAY) HOURS_IN_DAY else dailyPlayTimeHours
}
