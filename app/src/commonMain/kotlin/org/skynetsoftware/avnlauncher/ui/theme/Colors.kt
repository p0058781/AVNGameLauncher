package org.skynetsoftware.avnlauncher.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color

private val DarkGrey = Color(24, 26, 29)
private val LightGrey = Color(36, 38, 41)
private val Red = Color(186, 69, 69)

val darkColors = darkColors(
    primary = Red,
    secondary = Red,
    background = DarkGrey,
    surface = LightGrey,
    onPrimary = Color.White,
)