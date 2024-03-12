package org.skynetsoftware.avnlauncher.data.database

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.database.model.RealmGame
import org.skynetsoftware.avnlauncher.data.database.model.RealmLog

val databaseKoinModule = module {
    single<Realm> {
        val configManager = get<ConfigManager>()
        val configuration = RealmConfiguration.Builder(
            schema = setOf(RealmGame::class, RealmLog::class),
        ).directory(configManager.dataDir).schemaVersion(2).build()
        Realm.open(configuration)
    }
}