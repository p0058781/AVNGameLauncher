package org.skynetsoftware.avnlauncher.data.database

import org.koin.dsl.module

val databaseKoinModule = module {
    single<DatabaseFactory> { SqliteDatabaseFactory(get()) }
    single { get<DatabaseFactory>().createDatabase() }
}