package org.skynetsoftware.avnlauncher.config

import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder

internal expect fun Module.configKoinModuleInternal()

abstract class ConfigManagerShared {
    abstract val dataDir: String
    abstract val cacheDir: String
    open val selectedFilterDefault: Filter = Filter.All
    open val selectedSortOrderDefault: SortOrder = SortOrder.LastPlayed
    open val selectedSortOrderDirectionDefault: SortDirection = SortDirection.Descending
    abstract val sfwModeEnabledDefault: Boolean

    @Suppress("MagicNumber")
    open val updateCheckInterval: Long = 3_600_000L // 1 hour
    abstract val periodicUpdateChecksDefault: Boolean
    val minimizeToTrayOnCloseDefault: Boolean = false
}

expect abstract class ConfigManager : ConfigManagerShared
