package org.skynetsoftware.avnlauncher.data.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.Database

internal actual fun Module.driverFactoryKoinModule() {
    single<DriverFactory> { AndroidDriverFactory(get()) }
}

private class AndroidDriverFactory(private val context: Context) : DriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(Database.Schema, context, "avnlauncher.db")
    }
}
