package org.skynetsoftware.avnlauncher.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jthemedetecor.OsThemeDetector

@Composable
actual fun isDarkTheme(): Boolean {
    val themeDetector = OsThemeDetector.getDetector()
    var isDark by remember { mutableStateOf(themeDetector.isDark) }

    themeDetector.registerListener {
        isDark = it
    }

    return isDark
}
