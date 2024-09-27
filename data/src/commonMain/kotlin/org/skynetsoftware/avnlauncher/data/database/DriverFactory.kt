package org.skynetsoftware.avnlauncher.data.database

import app.cash.sqldelight.db.SqlDriver
import org.koin.core.module.Module

internal expect fun Module.driverFactoryKoinModule()

internal interface DriverFactory {
    fun createDriver(): SqlDriver
}
