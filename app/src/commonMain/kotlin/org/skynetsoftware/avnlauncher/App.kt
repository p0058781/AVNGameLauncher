package org.skynetsoftware.avnlauncher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.State
import androidx.compose.runtime.staticCompositionLocalOf
import org.skynetsoftware.avnlauncher.domain.model.Game

typealias DraggableArea = @Composable (content: @Composable () -> Unit) -> Unit
typealias MaximizeWindow = () -> Unit
typealias FloatingWindow = () -> Unit
typealias ExitApplication = () -> Unit

val LocalNavigator: ProvidableCompositionLocal<Navigator?> = staticCompositionLocalOf { null }

val LocalWindowControl: ProvidableCompositionLocal<WindowControl?> = staticCompositionLocalOf { null }
val LocalExitApplication: ProvidableCompositionLocal<ExitApplication?> = staticCompositionLocalOf { null }

class WindowControl(
    val draggableArea: DraggableArea,
    val maximizeWindow: MaximizeWindow,
    val floatingWindow: FloatingWindow,
    val windowFocused: State<Boolean>,
    val blockedByPopup: Boolean,
)

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
