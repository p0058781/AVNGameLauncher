package org.skynetsoftware.avnlauncher.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder
import org.skynetsoftware.avnlauncher.domain.utils.Option

abstract class ISettingsDefaults {
    open val selectedFilter: Filter = Filter.All
    open val selectedSortOrder: SortOrder = SortOrder.LastPlayed
    open val selectedSortOrderDirection: SortDirection = SortDirection.Descending
    open val sfwModeEnabled: Boolean = false

    @Suppress("MagicNumber")
    open val updateCheckInterval: Long = 3_600_000L // 1 hour
    open val periodicUpdateChecks: Boolean = true
    open val minimizeToTrayOnClose: Boolean = false
}

interface SettingsRepository {
    val selectedFilter: StateFlow<Filter>
    val selectedSortOrder: StateFlow<SortOrder>
    val selectedSortDirection: StateFlow<SortDirection>
    val gamesDir: Option<out StateFlow<String?>>
    val sfwModeEnabled: StateFlow<Boolean>
    val periodicUpdateChecksEnabled: StateFlow<Boolean>
    val updateCheckInterval: StateFlow<Long>
    val lastUpdateCheck: StateFlow<Long>
    val minimizeToTrayOnClose: Option<out StateFlow<Boolean>>

    suspend fun setSelectedFilter(filter: Filter)

    suspend fun setSelectedSortOrder(sortOrder: SortOrder)

    suspend fun setSelectedSortDirection(sortDirection: SortDirection)

    suspend fun setGamesDir(gamesDir: String)

    suspend fun setSfwModeEnabled(sfwModeEnabled: Boolean)

    suspend fun setPeriodicUpdateChecksEnabled(periodicUpdateChecksEnabled: Boolean)

    suspend fun setUpdateCheckInterval(updateCheckInterval: Long)

    suspend fun setLastUpdateCheck(lastUpdateCheck: Long)

    suspend fun setMinimizeToTrayOnClose(minimizeToTrayOnClose: Boolean)
}
