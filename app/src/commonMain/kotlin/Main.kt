import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import org.skynetsoftware.avnlauncher.App
import org.skynetsoftware.avnlauncher.LocalExitApplication
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.appName
import org.skynetsoftware.avnlauncher.app.generated.resources.systemNotificationDescriptionUpdateAvailable
import org.skynetsoftware.avnlauncher.app.generated.resources.systemNotificationTitleUpdateAvailable
import org.skynetsoftware.avnlauncher.app.generated.resources.trayCheckForUpdates
import org.skynetsoftware.avnlauncher.app.generated.resources.trayExit
import org.skynetsoftware.avnlauncher.app.generated.resources.trayShowHideWindow
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
import org.skynetsoftware.avnlauncher.server.HttpServer
import org.skynetsoftware.avnlauncher.server.httpServerKoinModule
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

private const val DEFAULT_DATA_DIR_NAME = "avnlauncher"
private const val HTTP_SERVER_STOP_TIMEOUT = 500L

@Suppress("LongMethod")
suspend fun main(args: Array<String>) {
    setAwtAppName()

    val parser = ArgParser("avnlauncher")
    val dataDir by parser.option(ArgType.String, shortName = "d", fullName = "data-dir", description = "Data dir")
    val cacheDir by parser.option(ArgType.String, shortName = "c", fullName = "cache-dir", description = "Cache dir")
    parser.parse(args)

    val config = createConfig(dataDir, cacheDir)
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
            httpServerKoinModule,
        )
    }

    logUncaughtExceptions(koinApplication.koin.get())

    application {
        val httpServer = koinInject<HttpServer>()
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
            LaunchedEffect(null) {
                settingsRepository.httpServerEnabled.collect { httpServerEnabled ->
                    if (httpServerEnabled) {
                        httpServer.start()
                    } else {
                        httpServer.stop(HTTP_SERVER_STOP_TIMEOUT)
                        delay(HTTP_SERVER_STOP_TIMEOUT)
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
                    icon = painterResource(if (hasUpdates) "tray_icon_updates.png" else "tray_icon.png"),
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

private fun createConfig(
    dataDir: String?,
    cacheDir: String?,
): Config {
    val dataDirFile = dataDir?.let { File(it) } ?: when (os) {
        OS.Linux -> File(System.getProperty("user.home"), ".config/$DEFAULT_DATA_DIR_NAME")
        OS.Windows -> File(System.getenv("AppData"), DEFAULT_DATA_DIR_NAME)
        OS.Mac -> File(System.getProperty("user.home"), "Library/Application Support/$DEFAULT_DATA_DIR_NAME")
    }
    val cacheDirFile = cacheDir?.let { File(it) } ?: when (os) {
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
