@file:Suppress("TooManyFunctions")

package org.skynetsoftware.avnlauncher.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.nHours
import org.skynetsoftware.avnlauncher.app.generated.resources.nMinutes
import org.skynetsoftware.avnlauncher.app.generated.resources.nSeconds
import org.skynetsoftware.avnlauncher.app.generated.resources.noValue
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZonedDateTime

private const val ONE_SECOND_MILLIS = 1000L
private const val ONE_HOUR_SECONDS = 3600L
private const val ONE_MINUTE_SECONDS = 60L
private const val HOURS_IN_DAY = 24f
private const val ONE_DAY_MS = 86400000f
private const val WEEK_NUMBER_OF_DAYS = 7L

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

fun Int.hoursToMilliseconds(): Long {
    return this * ONE_HOUR_SECONDS * ONE_SECOND_MILLIS
}

fun Long.millisecondsToHours(): Int {
    return (this / (ONE_HOUR_SECONDS * ONE_SECOND_MILLIS)).toInt()
}

fun calculateAveragePlayTime(
    firstPlayedTime: Long,
    lastPlayedTime: Long,
    totalPlayTime: Long,
): Float {
    val totalTimeDays = ((lastPlayedTime - firstPlayedTime) / ONE_DAY_MS).let { if (it < 1f) 1f else it }
    val totalPlayTimeDays = totalPlayTime / ONE_DAY_MS
    val dailyPlayTimeHours = (totalPlayTimeDays / totalTimeDays) * HOURS_IN_DAY
    return if (dailyPlayTimeHours > HOURS_IN_DAY) HOURS_IN_DAY else dailyPlayTimeHours
}

@Suppress("SwallowedException")
fun String.isValidDateTimePattern(): Boolean {
    return try {
        SimpleDateFormat(this)
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}

@Suppress("SwallowedException")
fun String.isValidDateTimeFormat(dateFormat: SimpleDateFormat): Boolean {
    return try {
        dateFormat.parse(this)
        true
    } catch (e: ParseException) {
        false
    }
}

/**
 * @return true if range start..end is between range rangeStart..rangeEnd
 * */
fun isTimeRangeBetween(
    rangeStart: Long,
    rangeEnd: Long,
    start: Long,
    end: Long,
): Boolean {
    return start in rangeStart..rangeEnd && end in rangeStart..rangeEnd
}

/**
 * @return true if time is between range rangeStart..rangeEnd
 * */
fun isTimeBetween(
    rangeStart: Long,
    rangeEnd: Long,
    time: Long,
): Boolean {
    return time in rangeStart..rangeEnd
}

fun ZonedDateTime.toMidnightMillis(): Long {
    return withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)
        .toInstant()
        .toEpochMilli()
}

fun ZonedDateTime.minus7DaysMillis(): Long {
    return minusDays(WEEK_NUMBER_OF_DAYS).toInstant().toEpochMilli()
}

fun ZonedDateTime.minus1MonthMillis(): Long {
    return minusMonths(1).toInstant().toEpochMilli()
}

fun ZonedDateTime.minus1YearMillis(): Long {
    return minusYears(1).toInstant().toEpochMilli()
}
