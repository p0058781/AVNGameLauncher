package org.skynetsoftware.avnlauncher

import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.skynetsoftware.avnlauncher.config.configManagerKoinModule
import org.skynetsoftware.avnlauncher.data.database.databaseKoinModule
import org.skynetsoftware.avnlauncher.data.gameImportKoinModule
import org.skynetsoftware.avnlauncher.data.repository.gamesRepositoryKoinModule
import org.skynetsoftware.avnlauncher.data.updateCheckerKoinModule
import org.skynetsoftware.avnlauncher.launcher.gameLauncherKoinModule
import org.skynetsoftware.avnlauncher.logging.Logger
import org.skynetsoftware.avnlauncher.logging.loggerKoinModule

object AVNLauncherApp {
    fun onCreate() {
        startKoin {
            modules(
                loggerKoinModule,
                databaseKoinModule,
                gamesRepositoryKoinModule,
                gameImportKoinModule,
                updateCheckerKoinModule,
                gameLauncherKoinModule,
                configManagerKoinModule,
            )
        }
    }
}
