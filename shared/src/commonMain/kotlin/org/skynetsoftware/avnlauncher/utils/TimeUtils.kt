package org.skynetsoftware.avnlauncher.utils

fun formatPlayTime(playTime: Long?): String {
    if(playTime == null || playTime == 0L) {
        return "-"
    }
    val totalSeconds = playTime / 1000

    // Calculate hours, minutes, and seconds
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    // Format the result
    return when {
        hours > 0 -> "$hours hours"
        minutes > 0 -> "$minutes minutes"
        else -> "$seconds seconds"
    }
}