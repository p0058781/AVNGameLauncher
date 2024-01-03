package org.skynetsoftware.avnlauncher.utils

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import org.skynetsoftware.avnlauncher.MR

@Composable
fun formatPlayTime(playTime: Long?): String {
    if (playTime == null || playTime == 0L) {
        return stringResource(MR.strings.noValue)
    }
    val totalSeconds = playTime / 1000

    // Calculate hours, minutes, and seconds
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    // Format the result
    return when {
        hours > 0 -> stringResource(MR.strings.nHours, hours)
        minutes > 0 -> stringResource(MR.strings.nMinutes, minutes)
        else -> stringResource(MR.strings.nSeconds, seconds)
    }
}
