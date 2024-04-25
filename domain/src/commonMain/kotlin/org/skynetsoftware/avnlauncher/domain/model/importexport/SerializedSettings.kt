package org.skynetsoftware.avnlauncher.domain.model.importexport

import kotlinx.serialization.Serializable
import org.skynetsoftware.avnlauncher.domain.model.GamesDisplayMode
import org.skynetsoftware.avnlauncher.domain.model.GridColumns
import org.skynetsoftware.avnlauncher.domain.model.LogLevel
import org.skynetsoftware.avnlauncher.domain.model.SortDirection

@Serializable
data class SerializedSettings(
    val selectedFilter: String?,
    val selectedSortOrder: String?,
    val selectedSortDirection: SortDirection?,
    val selectedGamesDisplayMode: GamesDisplayMode?,
    val gamesDir: String?,
    val sfwModeEnabled: Boolean?,
    val periodicUpdateChecksEnabled: Boolean?,
    val updateCheckInterval: Long?,
    val lastUpdateCheck: Long?,
    val minimizeToTrayOnClose: Boolean?,
    val startMinimized: Boolean?,
    val logLevel: LogLevel?,
    val showGifs: Boolean?,
    val dateFormat: String?,
    val timeFormat: String?,
    val gridColumns: GridColumns?,
    val systemNotificationsEnabled: Boolean?,
    val archivedGamesDisableUpdateChecks: Boolean?,
    val gridImageAspectRatio: Float?,
)