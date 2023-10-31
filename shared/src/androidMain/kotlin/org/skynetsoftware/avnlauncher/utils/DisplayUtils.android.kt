package org.skynetsoftware.avnlauncher.utils

import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

actual fun gamesGridCellMinSizeDp(): Dp {
    //TODO check if this is correct
    return (LocalConfiguration.current.smallestScreenWidthDp / 3).dp
}