package org.skynetsoftware.avnlauncher.config

import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.repository.Filter
import org.skynetsoftware.avnlauncher.data.repository.SortDirection
import org.skynetsoftware.avnlauncher.data.repository.SortOrder

expect val configKoinModule: Module

abstract class ConfigManagerShared {
    abstract val dataDir: String
    abstract val cacheDir: String
    abstract val remoteClientMode: Boolean
    open val selectedFilterDefault: Filter = Filter.All
    open val selectedSortOrderDefault: SortOrder = SortOrder.LastPlayed
    open val selectedSortOrderDirectionDefault: SortDirection = SortDirection.Descending
    open val fastUpdateCheckDefault: Boolean = false
    abstract val syncEnabledDefault: Boolean
    abstract val sfwModeEnabledDefault: Boolean
}
// TODO sync should be build type optional

expect abstract class ConfigManager : ConfigManagerShared
