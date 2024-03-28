package org.skynetsoftware.avnlauncher.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.GamesDisplayMode
import org.skynetsoftware.avnlauncher.domain.model.GridColumns
import org.skynetsoftware.avnlauncher.domain.model.LogLevel
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder
import org.skynetsoftware.avnlauncher.domain.utils.Option

abstract class ISettingsDefaults {
    open val selectedFilter: Filter = Filter.All
    open val selectedSortOrder: SortOrder = SortOrder.LastPlayed
    open val selectedSortOrderDirection: SortDirection = SortDirection.Descending
    open val selectedGamesDisplayMode: GamesDisplayMode = GamesDisplayMode.Grid
    open val sfwModeEnabled: Boolean = false

    @Suppress("MagicNumber")
    open val updateCheckInterval: Long = 3_600_000L // 1 hour
    open val periodicUpdateChecks: Boolean = true
    open val logLevel: LogLevel = LogLevel.Info
    open val showGifs: Boolean = false
    open val systemNotificationsEnabled: Boolean = true
    open val dateFormat: String = "MMM dd, yyyy"
    open val timeFormat: String = "HH:mm"
    open val gridColumns: GridColumns = GridColumns.Auto

    @Suppress("MagicNumber")
    open val gridImageAspectRatio: Float = 3.5f
    open val archivedGamesDisableUpdateChecks: Boolean = false
}

interface SettingsRepository {
    val selectedFilter: StateFlow<Filter>
    val selectedSortOrder: StateFlow<SortOrder>
    val selectedSortDirection: StateFlow<SortDirection>
    val selectedGamesDisplayMode: StateFlow<GamesDisplayMode>
    val gamesDir: Option<out StateFlow<String?>>
    val sfwModeEnabled: StateFlow<Boolean>
    val periodicUpdateChecksEnabled: StateFlow<Boolean>
    val updateCheckInterval: StateFlow<Long>
    val lastUpdateCheck: StateFlow<Long>
    val minimizeToTrayOnClose: Option<out StateFlow<Boolean>>
    val startMinimized: Option<out StateFlow<Boolean>>
    val logLevel: StateFlow<LogLevel>
    val showGifs: StateFlow<Boolean>
    val dateFormat: StateFlow<String>
    val timeFormat: StateFlow<String>
    val gridColumns: StateFlow<GridColumns>
    val systemNotificationsEnabled: StateFlow<Boolean>
    val archivedGamesDisableUpdateChecks: StateFlow<Boolean>
    val gridImageAspectRatio: StateFlow<Float>

    suspend fun setSelectedFilter(filter: Filter)

    suspend fun setSelectedSortOrder(sortOrder: SortOrder)

    suspend fun setSelectedSortDirection(sortDirection: SortDirection)

    suspend fun setSelectedGamesDisplayMode(gamesDisplayMode: GamesDisplayMode)

    suspend fun setGamesDir(gamesDir: String)

    suspend fun setSfwModeEnabled(sfwModeEnabled: Boolean)

    suspend fun setPeriodicUpdateChecksEnabled(periodicUpdateChecksEnabled: Boolean)

    suspend fun setUpdateCheckInterval(updateCheckInterval: Long)

    suspend fun setLastUpdateCheck(lastUpdateCheck: Long)

    suspend fun setMinimizeToTrayOnClose(minimizeToTrayOnClose: Boolean)

    suspend fun setStartMinimized(startMinimized: Boolean)

    suspend fun setLogLevel(logLevel: LogLevel)

    suspend fun setShowGifs(showGifs: Boolean)

    suspend fun setDateFormat(dateFormat: String)

    suspend fun setTimeFormat(timeFormat: String)

    suspend fun setGridColumns(gridColumns: GridColumns)

    suspend fun setSystemNotificationsEnabled(systemNotificationsEnabled: Boolean)

    suspend fun setArchivedGamesDisableUpdateChecks(archivedGamesDisableUpdateChecks: Boolean)

    suspend fun setGridImageAspectRatio(gridImageAspectRatio: Float)
}
