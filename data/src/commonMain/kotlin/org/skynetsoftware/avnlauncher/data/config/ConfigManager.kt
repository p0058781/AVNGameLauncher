package org.skynetsoftware.avnlauncher.data.config

import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder

internal expect fun Module.configKoinModule()

abstract class ConfigManagerShared {
    abstract val dataDir: String
    abstract val cacheDir: String
    open val selectedFilterDefault: Filter = Filter.All
    open val selectedSortOrderDefault: SortOrder = SortOrder.LastPlayed
    open val selectedSortOrderDirectionDefault: SortDirection = SortDirection.Descending
    open val fastUpdateCheckDefault: Boolean = false
    abstract val sfwModeEnabledDefault: Boolean
}
// TODO sync should be build type optional

expect abstract class ConfigManager : ConfigManagerShared
