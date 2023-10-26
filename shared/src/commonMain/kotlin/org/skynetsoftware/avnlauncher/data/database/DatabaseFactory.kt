package org.skynetsoftware.avnlauncher.data.database

import org.jetbrains.exposed.sql.Database
import org.skynetsoftware.avnlauncher.config.ConfigManager

interface DatabaseFactory {
    fun createDatabase(): Database
}

class SqliteDatabaseFactory(
    private val configManager: ConfigManager
) : DatabaseFactory {
    override fun createDatabase(): Database {
        val database = Database.connect("jdbc:sqlite:${configManager.databaseFile.absolutePath}", "org.sqlite.JDBC")
        if (!configManager.databaseFile.exists()) {
            //SchemaUtils.create(driver)
            //TODO
        }

        return database
    }
}