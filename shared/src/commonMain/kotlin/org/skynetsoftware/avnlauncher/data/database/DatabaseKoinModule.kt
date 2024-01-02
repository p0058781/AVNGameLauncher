package org.skynetsoftware.avnlauncher.data.database

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.dynamic.DynamicMutableRealmObject
import io.realm.kotlin.dynamic.DynamicRealmObject
import io.realm.kotlin.dynamic.getNullableValue
import io.realm.kotlin.dynamic.getValue
import io.realm.kotlin.dynamic.getValueSet
import io.realm.kotlin.migration.AutomaticSchemaMigration
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.database.model.RealmGame

val databaseKoinModule = module {
    single<Realm> {
        val configManager = get<ConfigManager>()
        val configuration = RealmConfiguration.Builder(
            schema = setOf(RealmGame::class),
        ).directory(configManager.dataDir).schemaVersion(5).migration(MyMigration()).build()
        Realm.open(configuration)
    }
}

private class MyMigration : AutomaticSchemaMigration {
    override fun migrate(migrationContext: AutomaticSchemaMigration.MigrationContext) {
        migrationContext.enumerate(RealmGame::class.simpleName!!) { oldObject: DynamicRealmObject, newObject: DynamicMutableRealmObject? ->
            newObject?.run {
                val executablePath: String? = oldObject.getNullableValue("executablePath")
                executablePath?.let {
                    getValueSet<String>("executablePaths").add(executablePath)
                }
            }
        }
    }
}
