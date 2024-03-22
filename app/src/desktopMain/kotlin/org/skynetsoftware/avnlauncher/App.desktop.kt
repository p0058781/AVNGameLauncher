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
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.appName
import org.skynetsoftware.avnlauncher.app.generated.resources.cardValuesScreenTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.customListsScreenTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.customStatusesScreenTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameDialogTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportScreenTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsTitle
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.ui.screen.cardvalues.CardValuesScreen
import org.skynetsoftware.avnlauncher.ui.screen.customlists.CustomListsScreen
import org.skynetsoftware.avnlauncher.ui.screen.customstatuses.CustomStatusesScreen
import org.skynetsoftware.avnlauncher.ui.screen.editgame.EditGameScreen
import org.skynetsoftware.avnlauncher.ui.screen.import.ImportGameScreen
import org.skynetsoftware.avnlauncher.ui.screen.importexport.ImportExportScreen
import org.skynetsoftware.avnlauncher.ui.screen.main.MainScreen
import org.skynetsoftware.avnlauncher.ui.screen.settings.SettingsScreen
import org.skynetsoftware.avnlauncher.ui.theme.darkColors
import java.awt.Dimension
import java.awt.Toolkit

private const val WINDOW_ICON = "icon.png"

private const val MAIN_WINDOW_DEFAULT_WIDTH_PERCENT = 0.7f
private const val MAIN_WINDOW_DEFAULT_HEIGHT_PERCENT = 0.72f

private const val SETTINGS_WINDOW_WIDTH_PERCENT = 0.4f
private const val SETTINGS_WINDOW_HEIGHT_PERCENT = 0.5f

private const val EDIT_GAME_WINDOW_WIDTH_PERCENT = 0.4f
private const val EDIT_GAME_WINDOW_HEIGHT_PERCENT = 0.5f

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
        size = getPercentageWindowSize(MAIN_WINDOW_DEFAULT_WIDTH_PERCENT, MAIN_WINDOW_DEFAULT_HEIGHT_PERCENT),
    )
    var showSettingsScreen by remember { mutableStateOf(false) }
    var showImportGameScreen by remember { mutableStateOf(false) }
    var showEditGameScreen by remember { mutableStateOf<Game?>(null) }
    var showImportExportScreen by remember { mutableStateOf(false) }
    var showCustomListsScreen by remember { mutableStateOf(false) }
    var showCustomStatusesScreen by remember { mutableStateOf(false) }
    var showCardValuesScreen by remember { mutableStateOf(false) }

    val exitApplication = LocalExitApplication.current

    CompositionLocalProvider(
        value = LocalNavigator provides object : Navigator {
            override fun navigateToSettings() {
                showSettingsScreen = true
            }

            override fun navigateToEditGame(game: Game) {
                showEditGameScreen = game
            }

            override fun navigateToImportGame() {
                showImportGameScreen = true
            }

            override fun navigateToImportExport() {
                showImportExportScreen = true
            }

            override fun navigateToCustomLists() {
                showCustomListsScreen = true
            }

            override fun navigateToCustomStatuses() {
                showCustomStatusesScreen = true
            }

            override fun navigateToCardValues() {
                showCardValuesScreen = true
            }
        },
    ) {
        Window(
            onCloseRequest = { exitApplication?.invoke() },
            title = stringResource(Res.string.appName),
            icon = painterResource(WINDOW_ICON),
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
            if (showSettingsScreen) {
                DialogWindow(
                    state = rememberDialogState(
                        size = getPercentageWindowSize(SETTINGS_WINDOW_WIDTH_PERCENT, SETTINGS_WINDOW_HEIGHT_PERCENT),
                    ),
                    resizable = false,
                    title = stringResource(Res.string.settingsTitle),
                    icon = painterResource(WINDOW_ICON),
                    onCloseRequest = {
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
                DialogWindow(
                    title = stringResource(Res.string.importGameDialogTitle),
                    icon = painterResource(WINDOW_ICON),
                    resizable = false,
                    onCloseRequest = {
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
                DialogWindow(
                    state = rememberDialogState(
                        size = getPercentageWindowSize(EDIT_GAME_WINDOW_WIDTH_PERCENT, EDIT_GAME_WINDOW_HEIGHT_PERCENT),
                    ),
                    title = stringResource(Res.string.editGameDialogTitle, it.title),
                    icon = painterResource(WINDOW_ICON),
                    resizable = false,
                    onCloseRequest = {
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
            if (showImportExportScreen) {
                DialogWindow(
                    resizable = false,
                    title = stringResource(Res.string.importExportScreenTitle),
                    icon = painterResource(WINDOW_ICON),
                    onCloseRequest = {
                        showImportExportScreen = false
                    },
                ) {
                    MaterialTheme(
                        colors = darkColors,
                    ) {
                        ImportExportScreen()
                    }
                }
            }
            if (showCustomListsScreen) {
                DialogWindow(
                    resizable = false,
                    title = stringResource(Res.string.customListsScreenTitle),
                    icon = painterResource(WINDOW_ICON),
                    onCloseRequest = {
                        showCustomListsScreen = false
                    },
                ) {
                    MaterialTheme(
                        colors = darkColors,
                    ) {
                        CustomListsScreen()
                    }
                }
            }
            if (showCustomStatusesScreen) {
                DialogWindow(
                    resizable = false,
                    title = stringResource(Res.string.customStatusesScreenTitle),
                    icon = painterResource(WINDOW_ICON),
                    onCloseRequest = {
                        showCustomStatusesScreen = false
                    },
                ) {
                    MaterialTheme(
                        colors = darkColors,
                    ) {
                        CustomStatusesScreen()
                    }
                }
            }
            if (showCardValuesScreen) {
                DialogWindow(
                    resizable = false,
                    title = stringResource(Res.string.cardValuesScreenTitle),
                    icon = painterResource(WINDOW_ICON),
                    onCloseRequest = {
                        showCardValuesScreen = false
                    },
                ) {
                    MaterialTheme(
                        colors = darkColors,
                    ) {
                        CardValuesScreen()
                    }
                }
            }
        }
    }
}

private fun getPercentageWindowSize(
    widthPercent: Float,
    heightPercent: Float,
): DpSize {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    val width: Int = (screenSize.width * widthPercent).toInt()
    val height: Int = (screenSize.height * heightPercent).toInt()
    return DpSize(width.dp, height.dp)
}
