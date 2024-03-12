package org.skynetsoftware.avnlauncher.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.awt.Toolkit

actual fun gamesGridCellMinSizeDp(): Dp {
    return (Toolkit.getDefaultToolkit().screenSize.width / 5).dp
}