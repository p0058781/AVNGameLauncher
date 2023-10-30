package org.skynetsoftware.avnlauncher.data.database

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.database.model.RealmGame
import org.skynetsoftware.avnlauncher.data.database.model.RealmLog

val databaseKoinModule = module {
    single<Realm> {
        val configuration = RealmConfiguration.create(schema = setOf(RealmGame::class, RealmLog::class))
        Realm.open(configuration)
    }
}