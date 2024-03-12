package org.skynetsoftware.avnlauncher.utils

import org.skynetsoftware.avnlauncher.resources.R

fun formatPlayTime(playTime: Long?): String {
    if(playTime == null || playTime == 0L) {
        return R.strings.noValue
    }
    val totalSeconds = playTime / 1000

    // Calculate hours, minutes, and seconds
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    // Format the result
    return when {
        hours > 0 -> R.strings.nHours.format(hours)
        minutes > 0 -> R.strings.nMinutes.format(minutes)
        else -> R.strings.nSeconds.format(seconds)
    }
}