package org.skynetsoftware.avnlauncher.data

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.config.configKoinModule
import org.skynetsoftware.avnlauncher.data.database.databaseKoinModule
import org.skynetsoftware.avnlauncher.data.f95.f95ApiKoinModule
import org.skynetsoftware.avnlauncher.data.f95.f95ParserKoinModule
import org.skynetsoftware.avnlauncher.data.repository.f95RepositoryKoinModule
import org.skynetsoftware.avnlauncher.data.repository.gamesRepositoryKoinModule
import org.skynetsoftware.avnlauncher.data.repository.settingsKoinModule

val dataKoinModule = module {
    databaseKoinModule()
    gamesRepositoryKoinModule()
    settingsKoinModule()
    configKoinModule()
    f95ParserKoinModule()
    f95ApiKoinModule()
    f95RepositoryKoinModule()
}
