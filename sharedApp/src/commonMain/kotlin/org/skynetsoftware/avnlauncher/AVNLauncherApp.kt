package org.skynetsoftware.avnlauncher

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.skynetsoftware.avnlauncher.data.dataKoinModule
import org.skynetsoftware.avnlauncher.data.gameImportKoinModule
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.imageloader.imageLoaderKoinModule
import org.skynetsoftware.avnlauncher.launcher.gameLauncherKoinModule
import org.skynetsoftware.avnlauncher.logger.logUncaughtExceptions
import org.skynetsoftware.avnlauncher.logger.loggerKoinModule
import org.skynetsoftware.avnlauncher.state.eventCenterModule
import org.skynetsoftware.avnlauncher.state.stateHandlerModule
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModelsKoinModule
import org.skynetsoftware.avnlauncher.updatechecker.UpdateChecker
import org.skynetsoftware.avnlauncher.updatechecker.updateCheckerKoinModule
import org.skynetsoftware.avnlauncher.utils.executableFinderKoinModule

object AVNLauncherApp {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun onCreate(initKoin: KoinApplication.() -> Unit = {}) {
        val koinApplication = startKoin {
            initKoin()
            modules(
                imageLoaderKoinModule(),
                dataKoinModule,
                loggerKoinModule,
                gameImportKoinModule,
                updateCheckerKoinModule,
                gameLauncherKoinModule,
                viewModelsKoinModule,
                eventCenterModule,
                stateHandlerModule,
                executableFinderKoinModule,
            )
        }

        logUncaughtExceptions(koinApplication.koin.get())

        val updateChecker = koinApplication.koin.get<UpdateChecker>()
        val settingsRepository = koinApplication.koin.get<SettingsRepository>()

        coroutineScope.launch {
            settingsRepository.periodicUpdateChecksEnabled.collect { periodicUpdateChecksEnabled ->
                if (periodicUpdateChecksEnabled) {
                    updateChecker.startPeriodicUpdateChecks()
                } else {
                    updateChecker.stopPeriodicUpdateChecks()
                }
            }
        }
    }
}
