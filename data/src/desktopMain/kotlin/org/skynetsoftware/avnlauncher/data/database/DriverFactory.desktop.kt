package org.skynetsoftware.avnlauncher.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.Database
import java.io.File
import java.util.Properties

internal actual fun Module.driverFactoryKoinModule() {
    single<DriverFactory> {
        DesktopDriverFactory(get())
    }
}

private class DesktopDriverFactory(private val configManager: ConfigManager) : DriverFactory {
    override fun createDriver(): SqlDriver {
        val databaseFile = File(configManager.dataDir, "avnlauncher.db")
        val driver: SqlDriver = JdbcSqliteDriver(
            url = "jdbc:sqlite:${databaseFile.absolutePath}",
            properties = Properties(),
            schema = Database.Schema,
        )
        if (!databaseFile.exists()) {
            Database.Schema.create(driver)
        }
        return driver
    }
}
