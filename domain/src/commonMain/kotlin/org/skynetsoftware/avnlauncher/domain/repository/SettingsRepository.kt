package org.skynetsoftware.avnlauncher.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder
import org.skynetsoftware.avnlauncher.domain.utils.Option

interface SettingsRepository {
    val selectedFilter: StateFlow<Filter>
    val selectedSortOrder: StateFlow<SortOrder>
    val selectedSortDirection: StateFlow<SortDirection>
    val gamesDir: Option<out StateFlow<String?>>
    val lastSyncTime: StateFlow<Long>
    val sfwModeEnabled: StateFlow<Boolean>
    val periodicUpdateChecksEnabled: StateFlow<Boolean>

    suspend fun setSelectedFilter(filter: Filter)

    suspend fun setSelectedSortOrder(sortOrder: SortOrder)

    suspend fun setSelectedSortDirection(sortDirection: SortDirection)

    suspend fun setGamesDir(gamesDir: String)

    suspend fun setLastSyncTime(lastSyncTime: Long)

    suspend fun setSfwModeEnabled(sfwModeEnabled: Boolean)

    suspend fun setPeriodicUpdateChecksEnabled(periodicUpdateChecksEnabled: Boolean)
}
