package org.skynetsoftware.avnlauncher

import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.appName
import org.skynetsoftware.avnlauncher.app.generated.resources.createCustomGameTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.customListsScreenTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.customStatusesScreenTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.icon
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsTitle
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.ui.screen.customlists.CustomListsScreen
import org.skynetsoftware.avnlauncher.ui.screen.customstatuses.CustomStatusesScreen
import org.skynetsoftware.avnlauncher.ui.screen.editgame.CreateCustomGameScreen
import org.skynetsoftware.avnlauncher.ui.screen.game.GameDetailsScreen
import org.skynetsoftware.avnlauncher.ui.screen.import.ImportGameScreen
import org.skynetsoftware.avnlauncher.ui.screen.main.MainScreen
import org.skynetsoftware.avnlauncher.ui.screen.settings.SettingsScreen
import org.skynetsoftware.avnlauncher.ui.theme.darkColors
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener

private val WINDOW_ICON = Res.drawable.icon

private const val MAIN_WINDOW_DEFAULT_WIDTH_PERCENT = 0.7f
private const val MAIN_WINDOW_DEFAULT_HEIGHT_PERCENT = 0.72f

private const val SETTINGS_WINDOW_WIDTH_PERCENT = 0.4f
private const val SETTINGS_WINDOW_HEIGHT_PERCENT = 0.5f

private const val EDIT_GAME_WINDOW_WIDTH_PERCENT = 0.4f
private const val EDIT_GAME_WINDOW_HEIGHT_PERCENT = 0.5f

private const val GAME_DETAILS_WINDOW_WIDTH_PERCENT = 0.4f
private const val GAME_DETAILS_WINDOW_HEIGHT_PERCENT = 0.5f

private const val CUSTOM_STATUSES_WINDOW_WIDTH_PERCENT = 0.4f
private const val CUSTOM_STATUSES_WINDOW_HEIGHT_PERCENT = 0.5f

private const val CUSTOM_LISTS_WINDOW_WIDTH_PERCENT = 0.4f
private const val CUSTOM_LISTS_WINDOW_HEIGHT_PERCENT = 0.5f

private val IMPORT_WINDOW_WIDTH = 500.dp
private val IMPORT_WINDOW_HEIGHT = 350.dp

typealias DraggableArea = @Composable (content: @Composable () -> Unit) -> Unit
typealias MaximizeWindow = () -> Unit
typealias FloatingWindow = () -> Unit
typealias ExitApplication = () -> Unit

val LocalNavigator: ProvidableCompositionLocal<Navigator?> = staticCompositionLocalOf { null }
val LocalWindowControl: ProvidableCompositionLocal<WindowControl?> = staticCompositionLocalOf { null }
val LocalExitApplication: ProvidableCompositionLocal<ExitApplication?> = staticCompositionLocalOf { null }

interface Navigator {
    fun navigateToSettings()

    fun navigateToGameDetails(game: Game)

    fun navigateToCreateCustomGame()

    fun navigateToImportGame()

    fun navigateToCustomLists()

    fun navigateToCustomStatuses()
}

class WindowControl(
    val draggableArea: DraggableArea,
    val maximizeWindow: MaximizeWindow,
    val floatingWindow: FloatingWindow,
    val windowFocused: State<Boolean>,
    val blockedByPopup: Boolean,
)

@Composable
@Suppress("CyclomaticComplexMethod")
fun App() {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        size = getPercentageWindowSize(MAIN_WINDOW_DEFAULT_WIDTH_PERCENT, MAIN_WINDOW_DEFAULT_HEIGHT_PERCENT),
    )
    var showSettingsScreen by remember { mutableStateOf(false) }
    var showImportGameScreen by remember { mutableStateOf(false) }
    var showGameDetailsScreen by remember { mutableStateOf<Game?>(null) }
    var showCustomListsScreen by remember { mutableStateOf(false) }
    var showCustomStatusesScreen by remember { mutableStateOf(false) }
    var showCreateCustomGameScreen by remember { mutableStateOf(false) }

    val exitApplication = LocalExitApplication.current

    CompositionLocalProvider(
        value = LocalNavigator provides object : Navigator {
            override fun navigateToSettings() {
                showSettingsScreen = true
            }

            override fun navigateToGameDetails(game: Game) {
                showGameDetailsScreen = game
            }

            override fun navigateToCreateCustomGame() {
                showCreateCustomGameScreen = true
            }

            override fun navigateToImportGame() {
                showImportGameScreen = true
            }

            override fun navigateToCustomLists() {
                showCustomListsScreen = true
            }

            override fun navigateToCustomStatuses() {
                showCustomStatusesScreen = true
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
            val windowFocused = remember { mutableStateOf(window.isFocused) }
            window.addWindowFocusListener(object : WindowFocusListener {
                override fun windowGainedFocus(e: WindowEvent) {
                    windowFocused.value = true
                }

                override fun windowLostFocus(e: WindowEvent) {
                    windowFocused.value = false
                }
            })
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
                        windowFocused = windowFocused,
                        blockedByPopup = showSettingsScreen || showImportGameScreen || showGameDetailsScreen != null ||
                            showImportGameScreen || showCustomListsScreen ||
                            showCreateCustomGameScreen,
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
                    state = rememberDialogState(
                        size = DpSize(width = IMPORT_WINDOW_WIDTH, height = IMPORT_WINDOW_HEIGHT),
                    ),
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
            showGameDetailsScreen?.let {
                DialogWindow(
                    state = rememberDialogState(
                        size = getPercentageWindowSize(
                            widthPercent = GAME_DETAILS_WINDOW_WIDTH_PERCENT,
                            heightPercent = GAME_DETAILS_WINDOW_HEIGHT_PERCENT,
                        ),
                    ),
                    title = it.title,
                    icon = painterResource(WINDOW_ICON),
                    resizable = false,
                    onCloseRequest = {
                        showGameDetailsScreen = null
                    },
                ) {
                    MaterialTheme(
                        colors = darkColors,
                    ) {
                        GameDetailsScreen(
                            gameId = it.f95ZoneThreadId,
                            onCloseRequest = {
                                showGameDetailsScreen = null
                            },
                        )
                    }
                }
            }
            if (showCreateCustomGameScreen) {
                DialogWindow(
                    state = rememberDialogState(
                        size = getPercentageWindowSize(EDIT_GAME_WINDOW_WIDTH_PERCENT, EDIT_GAME_WINDOW_HEIGHT_PERCENT),
                    ),
                    title = stringResource(Res.string.createCustomGameTitle),
                    icon = painterResource(WINDOW_ICON),
                    resizable = false,
                    onCloseRequest = {
                        showCreateCustomGameScreen = false
                    },
                ) {
                    MaterialTheme(
                        colors = darkColors,
                    ) {
                        CreateCustomGameScreen(
                            onCloseRequest = {
                                showCreateCustomGameScreen = false
                            },
                        )
                    }
                }
            }
            if (showCustomListsScreen) {
                DialogWindow(
                    state = rememberDialogState(
                        size = getPercentageWindowSize(
                            widthPercent = CUSTOM_LISTS_WINDOW_WIDTH_PERCENT,
                            heightPercent = CUSTOM_LISTS_WINDOW_HEIGHT_PERCENT,
                        ),
                    ),
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
                    state = rememberDialogState(
                        size = getPercentageWindowSize(
                            widthPercent = CUSTOM_STATUSES_WINDOW_WIDTH_PERCENT,
                            heightPercent = CUSTOM_STATUSES_WINDOW_HEIGHT_PERCENT,
                        ),
                    ),
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
