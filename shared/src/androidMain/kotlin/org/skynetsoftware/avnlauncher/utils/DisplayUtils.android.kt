package org.skynetsoftware.avnlauncher.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
actual fun gamesGridCellMinSizeDp(): Dp {
    return (LocalConfiguration.current.smallestScreenWidthDp / 2).dp
}
