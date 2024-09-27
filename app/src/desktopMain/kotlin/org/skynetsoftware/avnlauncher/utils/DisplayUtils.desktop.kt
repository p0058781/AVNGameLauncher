package org.skynetsoftware.avnlauncher.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.awt.Toolkit

private const val CELLS = 5

@Composable
actual fun gamesGridCellMinSizeDp(): Dp {
    return (Toolkit.getDefaultToolkit().screenSize.width / CELLS).dp
}
