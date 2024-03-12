package org.skynetsoftware.avnlauncher

import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
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
import org.skynetsoftware.avnlauncher.settings.settingsKoinModule
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModelsKoinModule

object AVNLauncherApp {
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
            )
        }

        logUncaughtExceptions(koinApplication.koin.get())
    }
}
