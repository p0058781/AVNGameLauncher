import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.AVNLauncherApp
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.domain.utils.Option
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.updatechecker.UpdateChecker
import java.awt.Dimension
import java.awt.Toolkit
import java.lang.reflect.Field
import java.util.Locale

private const val DEFAULT_WINDOW_WIDTH_PERCENT = 0.7f
private const val DEFAULT_WINDOW_HEIGHT_PERCENT = 0.72f

@Suppress("TooGenericExceptionCaught", "LongMethod")
fun main() {
    try {
        val xToolkit = Toolkit.getDefaultToolkit()
        val awtAppClassNameField: Field = xToolkit.javaClass.getDeclaredField("awtAppClassName")
        awtAppClassNameField.setAccessible(true)
        awtAppClassNameField.set(xToolkit, StringDesc.Resource(MR.strings.appName).localized())
    } catch (e: Exception) {
        @Suppress("PrintStackTrace")
        e.printStackTrace()
    }

    AVNLauncherApp.onCreate()
    application {
        val windowState = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getDefaultWindowSize(),
        )

        val updateChecker = koinInject<UpdateChecker>()
        val settingsRepository = koinInject<SettingsRepository>()
        // on desktop this is always Option.Some
        val minimizeToTrayOnClose by remember {
            (settingsRepository.minimizeToTrayOnClose as Option.Some).value
        }.collectAsState()
        var minimized by remember { mutableStateOf(false) }
        var open by remember { mutableStateOf(true) }

        val onCloseRequest = {
            if (minimizeToTrayOnClose) {
                minimized = true
            } else {
                exitApplication()
            }
        }

        if (open) {
            LaunchedEffect(null) {
                settingsRepository.periodicUpdateChecksEnabled.collect { periodicUpdateChecksEnabled ->
                    if (periodicUpdateChecksEnabled) {
                        updateChecker.startPeriodicUpdateChecks()
                    } else {
                        updateChecker.stopPeriodicUpdateChecks()
                    }
                }
            }
            if (minimizeToTrayOnClose) {
                val trayState = rememberTrayState()
                val eventCenter = koinInject<EventCenter>()

                LaunchedEffect(null) {
                    eventCenter.events.collect {
                        if (it is Event.UpdateCheckComplete) {
                            val count = it.updateCheckResult.updates.count { game -> game.updateAvailable }
                            if (count > 0) {
                                trayState.sendNotification(
                                    Notification(
                                        title = MR.strings.systemNotificationTitleUpdateAvailable.localized(),
                                        message = MR.strings.systemNotificationDescriptionUpdateAvailable.localized(
                                            Locale.getDefault(),
                                            count,
                                        ),
                                        type = Notification.Type.None,
                                    ),
                                )
                            }
                        }
                    }
                }
                Tray(
                    state = trayState,
                    icon = painterResource(R.images.appIcon),
                    menu = {
                        Item(
                            stringResource(MR.strings.trayShowHideWindow),
                            onClick = {
                                minimized = !minimized
                            },
                        )
                        Item(
                            stringResource(MR.strings.trayCheckForUpdates),
                            onClick = {
                                updateChecker.checkForUpdates(true)
                            },
                        )
                        Item(
                            stringResource(MR.strings.trayExit),
                            onClick = {
                                open = false
                                exitApplication()
                            },
                        )
                    },
                )
            }

            if (!minimized) {
                Window(
                    onCloseRequest = onCloseRequest,
                    title = stringResource(MR.strings.appName),
                    icon = painterResource(R.images.appIcon),
                    state = windowState,
                    undecorated = true,
                ) {
                    MainView(
                        exitApplication = onCloseRequest,
                        draggableArea = { content ->
                            WindowDraggableArea {
                                content()
                            }
                        },
                        setMaximized = {
                            windowState.placement = WindowPlacement.Maximized
                        },
                        setFloating = {
                            windowState.placement = WindowPlacement.Floating
                        },
                    )
                }
            }
        }
    }
}

fun getDefaultWindowSize(): DpSize {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    val width: Int = (screenSize.width * DEFAULT_WINDOW_WIDTH_PERCENT).toInt()
    val height: Int = (screenSize.height * DEFAULT_WINDOW_HEIGHT_PERCENT).toInt()
    return DpSize(width.dp, height.dp)
}
