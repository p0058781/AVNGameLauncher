package org.skynetsoftware.avnlauncher.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
actual fun gamesGridCellMinSizeDp(): Dp {
    //TODO check if this is correct
    return (LocalConfiguration.current.smallestScreenWidthDp / 2).dp
}