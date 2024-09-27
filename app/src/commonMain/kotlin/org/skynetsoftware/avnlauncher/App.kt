package org.skynetsoftware.avnlauncher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import org.skynetsoftware.avnlauncher.domain.model.Game

val LocalNavigator: ProvidableCompositionLocal<Navigator?> = staticCompositionLocalOf { null }

interface Navigator {
    fun navigateToSettings()

    fun navigateToGameDetails(game: Game)

    fun navigateToCreateCustomGame()

    fun navigateToImportGame()

    fun navigateToCustomLists()

    fun navigateToCustomStatuses()
}

@Composable
expect fun App()
