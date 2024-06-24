package org.skynetsoftware.avnlauncher.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.GamesDisplayMode
import org.skynetsoftware.avnlauncher.domain.model.GridColumns
import org.skynetsoftware.avnlauncher.domain.model.LogLevel
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder

@Suppress("ConstPropertyName")
object SettingsDefaults {
    val selectedFilter: Filter = Filter.All
    val selectedSortOrder: SortOrder = SortOrder.LastPlayed
    val selectedSortOrderDirection: SortDirection = SortDirection.Descending
    val selectedGamesDisplayMode: GamesDisplayMode = GamesDisplayMode.Grid
    const val sfwModeEnabled: Boolean = false

    @Suppress("MagicNumber")
    const val updateCheckInterval: Long = 3_600_000L // 1 hour
    const val periodicUpdateChecks: Boolean = true
    val logLevel: LogLevel = LogLevel.Info
    const val showGifs: Boolean = false
    const val systemNotificationsEnabled: Boolean = true
    const val dateFormat: String = "MMM dd, yyyy"
    const val timeFormat: String = "HH:mm"
    val gridColumns: GridColumns = GridColumns.Auto

    @Suppress("MagicNumber")
    const val gridImageAspectRatio: Float = 3.5f
    const val archivedGamesDisableUpdateChecks: Boolean = false

    const val minimizeToTrayOnClose: Boolean = false
    const val startMinimized: Boolean = false

    const val httpServerEnabled: Boolean = true
}

@Suppress("TooManyFunctions")
interface SettingsRepository {
    val selectedFilter: StateFlow<Filter>
    val selectedSortOrder: StateFlow<SortOrder>
    val selectedSortDirection: StateFlow<SortDirection>
    val selectedGamesDisplayMode: StateFlow<GamesDisplayMode>
    val gamesDir: StateFlow<String?>
    val sfwModeEnabled: StateFlow<Boolean>
    val periodicUpdateChecksEnabled: StateFlow<Boolean>
    val updateCheckInterval: StateFlow<Long>
    val lastUpdateCheck: StateFlow<Long>
    val minimizeToTrayOnClose: StateFlow<Boolean>
    val startMinimized: StateFlow<Boolean>
    val logLevel: StateFlow<LogLevel>
    val showGifs: StateFlow<Boolean>
    val dateFormat: StateFlow<String>
    val timeFormat: StateFlow<String>
    val gridColumns: StateFlow<GridColumns>
    val systemNotificationsEnabled: StateFlow<Boolean>
    val archivedGamesDisableUpdateChecks: StateFlow<Boolean>
    val gridImageAspectRatio: StateFlow<Float>
    val httpServerEnabled: StateFlow<Boolean>

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

    suspend fun setHttpServerEnabled(httpServerEnabled: Boolean)
}
