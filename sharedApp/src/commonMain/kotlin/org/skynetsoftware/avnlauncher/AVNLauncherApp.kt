package org.skynetsoftware.avnlauncher

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.skynetsoftware.avnlauncher.data.dataKoinModule
import org.skynetsoftware.avnlauncher.data.gameImportKoinModule
import org.skynetsoftware.avnlauncher.imageloader.imageLoaderKoinModule
import org.skynetsoftware.avnlauncher.launcher.gameLauncherKoinModule
import org.skynetsoftware.avnlauncher.logger.logUncaughtExceptions
import org.skynetsoftware.avnlauncher.logger.loggerKoinModule
import org.skynetsoftware.avnlauncher.state.eventCenterModule
import org.skynetsoftware.avnlauncher.state.stateHandlerModule
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModelsKoinModule
import org.skynetsoftware.avnlauncher.updatechecker.updateCheckerKoinModule
import org.skynetsoftware.avnlauncher.utils.executableFinderKoinModule

object AVNLauncherApp {
    fun onCreate(initKoin: KoinApplication.() -> Unit = {}) {
        val koinApplication = startKoin {
            initKoin()
            modules(
                imageLoaderKoinModule,
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
    }
}
