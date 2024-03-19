package org.skynetsoftware.avnlauncher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import org.skynetsoftware.avnlauncher.domain.model.Game

val LocalNavigator: ProvidableCompositionLocal<Navigator?> = staticCompositionLocalOf { null }

class Navigator(
    val navigateToSettings: () -> Unit,
    val navigateToEditGame: (game: Game) -> Unit,
    val navigateToImportGame: () -> Unit,
)

@Composable
expect fun App()
