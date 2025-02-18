import androidx.compose.runtime.*
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import com.sun.jna.Platform
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import org.skynetsoftware.avnlauncher.App
import org.skynetsoftware.avnlauncher.LocalExitApplication
import org.skynetsoftware.avnlauncher.app.generated.resources.*
import org.skynetsoftware.avnlauncher.appKoinModule
import org.skynetsoftware.avnlauncher.config.Config
import org.skynetsoftware.avnlauncher.configKoinModule
import org.skynetsoftware.avnlauncher.data.dataKoinModule
import org.skynetsoftware.avnlauncher.domain.domainKoinModule
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.domain.utils.OS
import org.skynetsoftware.avnlauncher.domain.utils.os
import org.skynetsoftware.avnlauncher.imageloader.imageLoaderKoinModule
import org.skynetsoftware.avnlauncher.launcher.gameLauncherKoinModule
import org.skynetsoftware.avnlauncher.link.externalLinkUtilsKoinModule
import org.skynetsoftware.avnlauncher.logger.logUncaughtExceptions
import org.skynetsoftware.avnlauncher.logger.loggerKoinModule
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.state.eventCenterModule
import org.skynetsoftware.avnlauncher.state.stateHandlerModule
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModelsKoinModule
import org.skynetsoftware.avnlauncher.updatechecker.UpdateChecker
import org.skynetsoftware.avnlauncher.updatechecker.updateCheckerKoinModule
import org.skynetsoftware.avnlauncher.utils.executableFinderKoinModule
import java.awt.Toolkit
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Method

private const val DEFAULT_DATA_DIR_NAME = "avnlauncher"

@Suppress("LongMethod")
suspend fun main() {
    setAwtAppName()

    println(getNativeLibraryResourcePrefix())
    println(System.getProperty("jna.library.path"))
    return

    val config = createConfig()
    System.setProperty("java.util.prefs.userRoot", config.dataDir)

    val koinApplication = startKoin {
        modules(
            imageLoaderKoinModule(),
            configKoinModule(config),
            appKoinModule,
            dataKoinModule,
            domainKoinModule,
            loggerKoinModule,
            updateCheckerKoinModule,
            gameLauncherKoinModule,
            viewModelsKoinModule,
            eventCenterModule,
            stateHandlerModule,
            executableFinderKoinModule,
            externalLinkUtilsKoinModule,
        )
    }

    logUncaughtExceptions(koinApplication.koin.get())

    application {
        val updateChecker = koinInject<UpdateChecker>()
        val settingsRepository = koinInject<SettingsRepository>()
        val minimizeToTrayOnClose by remember { settingsRepository.minimizeToTrayOnClose }.collectAsState()
        val startMinimized = settingsRepository.startMinimized.value
        var minimized by remember { mutableStateOf(minimizeToTrayOnClose && startMinimized) }
        var open by remember { mutableStateOf(true) }

        val onCloseRequest = {
            if (minimizeToTrayOnClose) {
                minimized = true
            } else {
                open = false
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
                var hasUpdates by remember { mutableStateOf(false) }

                LaunchedEffect(null) {
                    eventCenter.events.collect {
                        when (it) {
                            is Event.UpdateCheckComplete -> {
                                val count = it.updateCheckResult.updates.count { game -> game.updateAvailable }
                                if (count > 0 && settingsRepository.systemNotificationsEnabled.value) {
                                    trayState.sendNotification(
                                        Notification(
                                            title = getString(Res.string.systemNotificationTitleUpdateAvailable),
                                            message = getString(
                                                Res.string.systemNotificationDescriptionUpdateAvailable,
                                                count,
                                            ),
                                            type = Notification.Type.None,
                                        ),
                                    )
                                    hasUpdates = true
                                }
                            }

                            Event.UpdateSeen -> {
                                hasUpdates = false
                            }

                            else -> {}
                        }
                    }
                }
                Tray(
                    state = trayState,
                    icon = painterResource(if (hasUpdates) Res.drawable.tray_icon_updates else Res.drawable.tray_icon),
                    menu = {
                        Item(
                            stringResource(Res.string.trayShowHideWindow),
                            onClick = {
                                minimized = !minimized
                            },
                        )
                        Item(
                            stringResource(Res.string.trayCheckForUpdates),
                            onClick = {
                                updateChecker.checkForUpdates()
                            },
                        )
                        Item(
                            stringResource(Res.string.trayExit),
                            onClick = {
                                open = false
                                exitApplication()
                            },
                        )
                    },
                )
            }

            if (!minimized) {
                CompositionLocalProvider(
                    value = LocalExitApplication provides {
                        onCloseRequest()
                    },
                ) {
                    App()
                }
            }
        }
    }
}

@Suppress("TooGenericExceptionCaught")
private suspend fun setAwtAppName() {
    try {
        val xToolkit = Toolkit.getDefaultToolkit()
        val awtAppClassNameField: Field = xToolkit.javaClass.getDeclaredField("awtAppClassName")
        awtAppClassNameField.setAccessible(true)
        awtAppClassNameField.set(xToolkit, getString(Res.string.appName))
    } catch (e: Exception) {
        @Suppress("PrintStackTrace")
        e.printStackTrace()
    }
}

@Suppress("TooGenericExceptionCaught")
private fun getNativeLibraryResourcePrefix(): String? {
    return try {
        val getNativeLibraryResourcePrefixMethodName: Method = Platform::class.java.getDeclaredMethod("getNativeLibraryResourcePrefix")
        getNativeLibraryResourcePrefixMethodName.setAccessible(true)
        getNativeLibraryResourcePrefixMethodName.invoke(Platform::class) as? String
    } catch (e: Exception) {
        @Suppress("PrintStackTrace")
        e.printStackTrace()
        null
    }
}

private fun createConfig(): Config {
    val dataDirFile = when (os) {
        OS.Linux -> File(System.getProperty("user.home"), ".config/$DEFAULT_DATA_DIR_NAME")
        OS.Windows -> File(System.getenv("AppData"), DEFAULT_DATA_DIR_NAME)
        OS.Mac -> File(System.getProperty("user.home"), "Library/Application Support/$DEFAULT_DATA_DIR_NAME")
    }
    val cacheDirFile = when (os) {
        OS.Linux -> File(System.getProperty("user.home"), ".cache/$DEFAULT_DATA_DIR_NAME")
        OS.Windows -> File(System.getenv("AppData"), "$DEFAULT_DATA_DIR_NAME/cache")
        OS.Mac -> File(System.getProperty("user.home"), "Library/Caches/$DEFAULT_DATA_DIR_NAME")
    }
    dataDirFile.mkdirs()
    cacheDirFile.mkdirs()
    return Config(
        dataDir = dataDirFile.absolutePath,
        cacheDir = cacheDirFile.absolutePath,
    )
}
