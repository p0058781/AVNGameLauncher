package org.skynetsoftware.avnlauncher.data

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.database.databaseKoinModule
import org.skynetsoftware.avnlauncher.data.database.driverFactoryKoinModule
import org.skynetsoftware.avnlauncher.data.f95.f95ApiKoinModule
import org.skynetsoftware.avnlauncher.data.f95.f95ParserKoinModule
import org.skynetsoftware.avnlauncher.data.repository.f95RepositoryKoinModule
import org.skynetsoftware.avnlauncher.data.repository.gameListsRepositoryKoinModule
import org.skynetsoftware.avnlauncher.data.repository.gamesRepositoryKoinModule
import org.skynetsoftware.avnlauncher.data.repository.playSessionRepositoryKoinModule
import org.skynetsoftware.avnlauncher.data.repository.playStateRepositoryKoinModule
import org.skynetsoftware.avnlauncher.data.repository.settingsKoinModule

val dataKoinModule = module {
    driverFactoryKoinModule()
    databaseKoinModule()
    gamesRepositoryKoinModule()
    playStateRepositoryKoinModule()
    gameListsRepositoryKoinModule()
    playSessionRepositoryKoinModule()
    settingsKoinModule()
    f95ParserKoinModule()
    f95ApiKoinModule()
    f95RepositoryKoinModule()
}
