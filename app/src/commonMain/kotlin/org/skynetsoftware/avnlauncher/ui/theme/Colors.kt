package org.skynetsoftware.avnlauncher.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color

private val DarkGrey = Color(24, 26, 29)
private val LightGrey = Color(36, 38, 41)
private val Red = Color(186, 69, 69)
val Warning = Color(233, 213, 2)
val UpdateAvailable = Color(60, 242, 14)

val darkColors = darkColors(
    primary = Red,
    secondary = Red,
    background = DarkGrey,
    surface = LightGrey,
    onPrimary = Color.White,
)
