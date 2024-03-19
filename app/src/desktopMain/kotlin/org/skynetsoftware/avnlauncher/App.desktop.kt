package org.skynetsoftware.avnlauncher

import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.appName
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameDialogTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.settings
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.ui.screen.Dialog
import org.skynetsoftware.avnlauncher.ui.screen.editgame.EditGameScreen
import org.skynetsoftware.avnlauncher.ui.screen.import.ImportGameScreen
import org.skynetsoftware.avnlauncher.ui.screen.main.MainScreen
import org.skynetsoftware.avnlauncher.ui.screen.settings.SettingsScreen
import org.skynetsoftware.avnlauncher.ui.theme.darkColors
import java.awt.Dimension
import java.awt.Toolkit

private const val DEFAULT_WINDOW_WIDTH_PERCENT = 0.7f
private const val DEFAULT_WINDOW_HEIGHT_PERCENT = 0.72f

typealias DraggableArea = @Composable (content: @Composable () -> Unit) -> Unit
typealias MaximizeWindow = () -> Unit
typealias FloatingWindow = () -> Unit
typealias ExitApplication = () -> Unit

val LocalWindowControl: ProvidableCompositionLocal<WindowControl?> = staticCompositionLocalOf { null }
val LocalExitApplication: ProvidableCompositionLocal<ExitApplication?> = staticCompositionLocalOf { null }

class WindowControl(
    val draggableArea: DraggableArea,
    val maximizeWindow: MaximizeWindow,
    val floatingWindow: FloatingWindow,
)

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun App() {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        size = getDefaultWindowSize(),
    )
    var showSettingsScreen by remember { mutableStateOf(false) }
    var showImportGameScreen by remember { mutableStateOf(false) }
    var showEditGameScreen by remember { mutableStateOf<Game?>(null) }

    val exitApplication = LocalExitApplication.current

    CompositionLocalProvider(
        value = LocalNavigator provides Navigator(
            navigateToSettings = { showSettingsScreen = true },
            navigateToImportGame = { showImportGameScreen = true },
            navigateToEditGame = { showEditGameScreen = it },
        ),
    ) {
        Window(
            onCloseRequest = { exitApplication?.invoke() },
            title = stringResource(Res.string.appName),
            icon = painterResource("icon.png"),
            state = windowState,
            undecorated = true,
        ) {
            MaterialTheme(
                colors = darkColors,
            ) {
                CompositionLocalProvider(
                    value = LocalWindowControl provides WindowControl(
                        draggableArea = { content ->
                            WindowDraggableArea {
                                content()
                            }
                        },
                        maximizeWindow = {
                            windowState.placement = WindowPlacement.Maximized
                        },
                        floatingWindow = {
                            windowState.placement = WindowPlacement.Floating
                        },
                    ),
                ) {
                    MainScreen()
                }
            }
        }
        if (showSettingsScreen) {
            Dialog(
                title = stringResource(Res.string.settings),
                onDismiss = {
                    showSettingsScreen = false
                },
            ) {
                MaterialTheme(
                    colors = darkColors,
                ) {
                    SettingsScreen()
                }
            }
        }
        if (showImportGameScreen) {
            Dialog(
                title = stringResource(Res.string.importGameDialogTitle),
                onDismiss = {
                    showImportGameScreen = false
                },
            ) {
                MaterialTheme(
                    colors = darkColors,
                ) {
                    ImportGameScreen {
                        showImportGameScreen = false
                    }
                }
            }
        }
        showEditGameScreen?.let {
            Dialog(
                title = stringResource(Res.string.editGameDialogTitle, it.title),
                onDismiss = {
                    showEditGameScreen = null
                },
            ) {
                MaterialTheme(
                    colors = darkColors,
                ) {
                    EditGameScreen(
                        gameId = it.f95ZoneThreadId,
                        onCloseRequest = {
                            showEditGameScreen = null
                        },
                    )
                }
            }
        }
    }
}

private fun getDefaultWindowSize(): DpSize {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    val width: Int = (screenSize.width * DEFAULT_WINDOW_WIDTH_PERCENT).toInt()
    val height: Int = (screenSize.height * DEFAULT_WINDOW_HEIGHT_PERCENT).toInt()
    return DpSize(width.dp, height.dp)
}
