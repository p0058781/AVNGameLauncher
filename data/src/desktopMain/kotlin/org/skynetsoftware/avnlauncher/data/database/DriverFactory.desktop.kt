package org.skynetsoftware.avnlauncher.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.Database
import org.skynetsoftware.avnlauncher.data.config.ConfigManager
import java.io.File

internal actual fun Module.driverFactoryKoinModule() {
    single<DriverFactory> {
        DesktopDriverFactory(get())
    }
}

private class DesktopDriverFactory(private val configManager: ConfigManager) : DriverFactory {
    override fun createDriver(): SqlDriver {
        val databaseFile = File(configManager.dataDir, "avnlauncher.db")
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")
        if (!databaseFile.exists()) {
            Database.Schema.create(driver)
        }
        return driver
    }
}
