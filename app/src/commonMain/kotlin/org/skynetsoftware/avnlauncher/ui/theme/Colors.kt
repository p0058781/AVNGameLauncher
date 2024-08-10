package org.skynetsoftware.avnlauncher.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color

private val DarkGrey = Color(24, 26, 29)
private val LightGrey = Color(36, 38, 41)
private val Red = Color(186, 69, 69)
val Warning = Color(233, 213, 2)
val UpdateAvailable = Color(60, 242, 14)
val GameRunningFade = Color(0xaa000000)

val PrefixAbandonedFill = Color(0xff, 0x98, 0x00)
val PrefixAbandonedBorder = Color(0xff, 0xad, 0x33)

val PrefixDefaultFill = Color(0x61, 0x61, 0x61)
val PrefixDefaultBorder = Color(0x7a, 0x7a, 0x7a)

val PrefixRenPyFill = Color(0xb0, 0x68, 0xe8)

val PrefixVNFill = Color(0xd3, 0x2f, 0x2f)
val PrefixVNBorder = Color(0xdc, 0x59, 0x59)

val PrefixOthersFill = Color(0x8b, 0xc3, 0x4a)
val PrefixOthersBorder = Color(0xa4, 0xd0, 0x70)

val PrefixCompletedFill = Color(0x21, 0x96, 0xf3)
val PrefixCompletedBorder = Color(0x51, 0xad, 0xf6)

val PrefixHtmlFill = Color(0x68, 0x9f, 0x38)
val PrefixHtmlBorder = Color(0x81, 0xbf, 0x4b)

val PrefixWolfRpgFill = Color(0x4c, 0xaf, 0x50)
val PrefixWolfRpgBorder = Color(0x6e, 0xc0, 0x71)

val PrefixUnrealFill = Color(0x0d, 0x47, 0xa1)
val PrefixUnrealBorder = Color(0x11, 0x5c, 0xd0)

val PrefixOnHoldFill = Color(0x03, 0xa9, 0xf4)
val PrefixOnHoldBorder = Color(0x2e, 0xbc, 0xfc)

val PrefixUnityFill = Color(0xFE, 0x59, 0x01)
val PrefixJavaFill = Color(0x52, 0xA6, 0xB0)

val darkColors = darkColors(
    primary = Red,
    secondary = Red,
    background = DarkGrey,
    surface = LightGrey,
    onPrimary = Color.White,
)
