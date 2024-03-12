package org.skynetsoftware.avnlauncher.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.awt.Toolkit

@Composable
actual fun gamesGridCellMinSizeDp(): Dp {
    return (Toolkit.getDefaultToolkit().screenSize.width / 5).dp
}
