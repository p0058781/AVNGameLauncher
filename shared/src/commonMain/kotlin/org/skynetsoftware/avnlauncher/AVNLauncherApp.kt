package org.skynetsoftware.avnlauncher

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.skynetsoftware.avnlauncher.config.configKoinModule
import org.skynetsoftware.avnlauncher.data.database.databaseKoinModule
import org.skynetsoftware.avnlauncher.data.gameImportKoinModule
import org.skynetsoftware.avnlauncher.data.repository.gamesRepositoryKoinModule
import org.skynetsoftware.avnlauncher.data.updateCheckerKoinModule
import org.skynetsoftware.avnlauncher.f95.f95ApiKoinModule
import org.skynetsoftware.avnlauncher.f95.f95ParserKoinModule
import org.skynetsoftware.avnlauncher.imageloader.imageLoaderKoinModule
import org.skynetsoftware.avnlauncher.launcher.gameLauncherKoinModule
import org.skynetsoftware.avnlauncher.logging.logUncaughtExceptions
import org.skynetsoftware.avnlauncher.logging.loggerKoinModule
import org.skynetsoftware.avnlauncher.settings.SettingsManager
import org.skynetsoftware.avnlauncher.settings.settingsKoinModule
import org.skynetsoftware.avnlauncher.state.eventCenterModule
import org.skynetsoftware.avnlauncher.state.stateHandlerModule
import org.skynetsoftware.avnlauncher.sync.SyncService
import org.skynetsoftware.avnlauncher.sync.syncApiKoinModule
import org.skynetsoftware.avnlauncher.sync.syncServiceModule
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModelsKoinModule

object AVNLauncherApp {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun onCreate(initKoin: KoinApplication.() -> Unit = {}) {
        val koinApplication = startKoin {
            initKoin()
            modules(
                imageLoaderKoinModule,
                settingsKoinModule,
                loggerKoinModule,
                databaseKoinModule,
                gamesRepositoryKoinModule,
                gameImportKoinModule,
                updateCheckerKoinModule,
                gameLauncherKoinModule,
                configKoinModule,
                f95ApiKoinModule,
                f95ParserKoinModule,
                viewModelsKoinModule,
                eventCenterModule,
                stateHandlerModule,
                syncApiKoinModule,
                syncServiceModule,
            )
        }

        logUncaughtExceptions(koinApplication.koin.get())

        val syncService = koinApplication.koin.get<SyncService>()
        val settingsManager = koinApplication.koin.get<SettingsManager>()

        coroutineScope.launch {
            settingsManager.syncEnabled.collect { syncEnabled ->
                if (syncEnabled) {
                    syncService.start()
                } else {
                    syncService.stop()
                }
            }
        }
    }
}
