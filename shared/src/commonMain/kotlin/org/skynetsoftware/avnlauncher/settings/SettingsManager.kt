package org.skynetsoftware.avnlauncher.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.repository.Filter
import org.skynetsoftware.avnlauncher.data.repository.SortDirection
import org.skynetsoftware.avnlauncher.data.repository.SortOrder

val settingsKoinModule = module {
    single<SettingsManager> { SettingsManagerImpl(Settings()) }
}

interface SettingsManager {
    var selectedFilter: Filter
    var selectedSortOrder: SortOrder
    var selectedSortDirection: SortDirection
}

class SettingsManagerImpl(private val settings: Settings): SettingsManager {
    override var selectedFilter: Filter
        get() {
            val selectedFilterClassname = settings.getString("selectedFilter", Filter.All::class.simpleName ?: "All")
            return Filter.entries.find { it::class.simpleName == selectedFilterClassname } ?: Filter.All
        }
        set(value) {
            settings["selectedFilter"] = value::class.simpleName
        }

    override var selectedSortOrder: SortOrder
        get() {
            val selectedSortOrderClassname = settings.getString("selectedSortOrder", SortOrder.LastPlayed::class.simpleName ?: "LastPlayed")
            return SortOrder.entries.find { it::class.simpleName == selectedSortOrderClassname } ?: SortOrder.LastPlayed
        }
        set(value) {
            settings["selectedSortOrder"] = value::class.simpleName
        }

    override var selectedSortDirection: SortDirection
        get() {
            val selectedSortDirectionClassname = settings.getString("selectedSortDirection", SortDirection.Descending.name)
            return SortDirection.entries.find { it.name == selectedSortDirectionClassname } ?: SortDirection.Descending
        }
        set(value) {
            settings["selectedSortDirection"] = value.name
        }
}

//TODO [medium] import game executable search location for desktop