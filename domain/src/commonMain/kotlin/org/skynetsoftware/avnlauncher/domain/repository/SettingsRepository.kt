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
    val fastUpdateCheck: StateFlow<Boolean>
    val gamesDir: Option<out StateFlow<String?>>
    val lastSyncTime: StateFlow<Long>
    val sfwModeEnabled: StateFlow<Boolean>
    val forceDarkTheme: StateFlow<Boolean>

    suspend fun setSelectedFilter(filter: Filter)

    suspend fun setSelectedSortOrder(sortOrder: SortOrder)

    suspend fun setSelectedSortDirection(sortDirection: SortDirection)

    suspend fun setFastUpdateCheck(fastUpdateCheck: Boolean)

    suspend fun setGamesDir(gamesDir: String)

    suspend fun setLastSyncTime(lastSyncTime: Long)

    suspend fun setSfwModeEnabled(sfwModeEnabled: Boolean)

    suspend fun setForceDarkTheme(forceDarkTheme: Boolean)
}
