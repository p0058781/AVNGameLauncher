package org.skynetsoftware.avnlauncher.data.repository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.domain.model.GamesDisplayMode
import org.skynetsoftware.avnlauncher.domain.model.GridColumns
import org.skynetsoftware.avnlauncher.domain.model.LogLevel
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder
import org.skynetsoftware.avnlauncher.domain.repository.ISettingsDefaults
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.domain.utils.MutableStateFlow

internal expect fun Module.settingsKoinModule()

expect object SettingsDefaults : ISettingsDefaults

abstract class SettingsRepositoryShared internal constructor(
    private val settings: Settings,
) : SettingsRepository {

    private val _gamesDir =
        MutableStateFlow {
            val value = settings.getString(
                SettingsRepository::gamesDir.name,
                "",
            )
            value.ifBlank {
                null
            }
        }
    override val gamesDir: StateFlow<String?> get() = _gamesDir

        private val _selectedFilterName = MutableStateFlow {
            settings.getString(
                SettingsRepository::selectedFilterName.name,
                SettingsDefaults.selectedFilter,
            )
    }
    override val selectedFilterName: StateFlow<String> get() = _selectedFilterName

    private val _selectedFilterData = MutableStateFlow {
        settings.getStringOrNull(SettingsRepository::selectedFilterData.name)
    }
    override val selectedFilterData: StateFlow<String?> get() = _selectedFilterData

    private val _selectedSortOrder = MutableStateFlow {
        val selectedSortOrderClassname = settings.getString(
            SettingsRepository::selectedSortOrder.name,
            SettingsDefaults.selectedSortOrder::class.simpleName!!,
        )
        SortOrder.entries.find { it::class.simpleName == selectedSortOrderClassname }
            ?: SettingsDefaults.selectedSortOrder
    }
    override val selectedSortOrder: StateFlow<SortOrder> get() = _selectedSortOrder

    private val _selectedSortDirection = MutableStateFlow {
        val selectedSortDirectionClassname =
            settings.getString(
                SettingsRepository::selectedSortDirection.name,
                SettingsDefaults.selectedSortOrderDirection.name,
            )
        SortDirection.entries.find { it.name == selectedSortDirectionClassname }
            ?: SettingsDefaults.selectedSortOrderDirection
    }
    override val selectedSortDirection: StateFlow<SortDirection> get() = _selectedSortDirection

    private val _selectedGamesDisplayMode = MutableStateFlow {
        val selectedGamesDisplayModeClassname =
            settings.getString(
                SettingsRepository::selectedGamesDisplayMode.name,
                SettingsDefaults.selectedGamesDisplayMode.name,
            )
        GamesDisplayMode.entries.find { it.name == selectedGamesDisplayModeClassname }
            ?: SettingsDefaults.selectedGamesDisplayMode
    }
    override val selectedGamesDisplayMode: StateFlow<GamesDisplayMode> get() = _selectedGamesDisplayMode

    private val _lastUpdateCheck =
        MutableStateFlow { settings.getLong(SettingsRepository::lastUpdateCheck.name, 0L) }
    override val lastUpdateCheck: StateFlow<Long> get() = _lastUpdateCheck

    private val _sfwModeEnabled =
        MutableStateFlow {
            settings.getBoolean(
                SettingsRepository::sfwModeEnabled.name,
                SettingsDefaults.sfwModeEnabled,
            )
        }
    override val sfwModeEnabled: StateFlow<Boolean> get() = _sfwModeEnabled

    private val _periodicUpdateChecksEnabled =
        MutableStateFlow {
            settings.getBoolean(
                SettingsRepository::periodicUpdateChecksEnabled.name,
                SettingsDefaults.periodicUpdateChecks,
            )
        }
    override val periodicUpdateChecksEnabled: StateFlow<Boolean> get() = _periodicUpdateChecksEnabled

    private val _updateCheckInterval =
        MutableStateFlow {
            settings.getLong(
                SettingsRepository::updateCheckInterval.name,
                SettingsDefaults.updateCheckInterval,
            )
        }
    override val updateCheckInterval: StateFlow<Long> get() = _updateCheckInterval

    private val _logLevel = MutableStateFlow {
        val logLevelClassname =
            settings.getString(
                SettingsRepository::logLevel.name,
                SettingsDefaults.logLevel.name,
            )
        LogLevel.entries.find { it.name == logLevelClassname }
            ?: SettingsDefaults.logLevel
    }
    override val logLevel: StateFlow<LogLevel> get() = _logLevel

    private val _systemNotificationsEnabled =
        MutableStateFlow {
            settings.getBoolean(
                SettingsRepository::systemNotificationsEnabled.name,
                SettingsDefaults.systemNotificationsEnabled,
            )
        }
    override val systemNotificationsEnabled: StateFlow<Boolean> get() = _systemNotificationsEnabled

    private val _archivedGamesDisableUpdateChecks =
        MutableStateFlow {
            settings.getBoolean(
                SettingsRepository::archivedGamesDisableUpdateChecks.name,
                SettingsDefaults.archivedGamesDisableUpdateChecks,
            )
        }
    override val archivedGamesDisableUpdateChecks: StateFlow<Boolean> get() = _archivedGamesDisableUpdateChecks

    private val _dateFormat =
        MutableStateFlow {
            settings.getString(
                SettingsRepository::dateFormat.name,
                SettingsDefaults.dateFormat,
            )
        }
    override val dateFormat: StateFlow<String> get() = _dateFormat

    private val _timeFormat =
        MutableStateFlow {
            settings.getString(
                SettingsRepository::timeFormat.name,
                SettingsDefaults.timeFormat,
            )
        }
    override val timeFormat: StateFlow<String> get() = _timeFormat

    private val _gridColumns = MutableStateFlow {
        val gridColumnsClassname =
            settings.getString(
                SettingsRepository::gridColumns.name,
                SettingsDefaults.gridColumns.name,
            )
        GridColumns.entries.find { it.name == gridColumnsClassname }
            ?: SettingsDefaults.gridColumns
    }
    override val gridColumns: StateFlow<GridColumns> get() = _gridColumns

    private val _gridImageAspectRatio =
        MutableStateFlow {
            settings.getFloat(
                SettingsRepository::gridImageAspectRatio.name,
                SettingsDefaults.gridImageAspectRatio,
            )
        }
    override val gridImageAspectRatio: StateFlow<Float> get() = _gridImageAspectRatio

    override suspend fun setSelectedFilterName(filterName: String) {
        _selectedFilterName.emit(filterName)
        settings[SettingsRepository::selectedFilterName.name] = filterName
    }

    override suspend fun setSelectedFilterData(filterData: String?) {
        _selectedFilterData.emit(filterData)
        settings[SettingsRepository::selectedFilterData.name] = filterData
    }

    override suspend fun setSelectedSortOrder(sortOrder: SortOrder) {
        _selectedSortOrder.emit(sortOrder)
        settings[SettingsRepository::selectedSortOrder.name] = sortOrder::class.simpleName
    }

    override suspend fun setSelectedSortDirection(sortDirection: SortDirection) {
        _selectedSortDirection.emit(sortDirection)
        settings[SettingsRepository::selectedSortDirection.name] = sortDirection.name
    }

    override suspend fun setSelectedGamesDisplayMode(gamesDisplayMode: GamesDisplayMode) {
        _selectedGamesDisplayMode.emit(gamesDisplayMode)
        settings[SettingsRepository::selectedGamesDisplayMode.name] = gamesDisplayMode.name
    }

    override suspend fun setLastUpdateCheck(lastUpdateCheck: Long) {
        _lastUpdateCheck.emit(lastUpdateCheck)
        settings[SettingsRepository::lastUpdateCheck.name] = lastUpdateCheck
    }

    override suspend fun setSfwModeEnabled(sfwModeEnabled: Boolean) {
        _sfwModeEnabled.emit(sfwModeEnabled)
        settings[SettingsRepository::sfwModeEnabled.name] = sfwModeEnabled
    }

    override suspend fun setPeriodicUpdateChecksEnabled(periodicUpdateChecksEnabled: Boolean) {
        _periodicUpdateChecksEnabled.emit(periodicUpdateChecksEnabled)
        settings[SettingsRepository::periodicUpdateChecksEnabled.name] = periodicUpdateChecksEnabled
    }

    override suspend fun setUpdateCheckInterval(updateCheckInterval: Long) {
        _updateCheckInterval.emit(updateCheckInterval)
        settings[SettingsRepository::updateCheckInterval.name] = updateCheckInterval
    }

    override suspend fun setLogLevel(logLevel: LogLevel) {
        _logLevel.emit(logLevel)
        settings[SettingsRepository::logLevel.name] = logLevel.name
    }

    override suspend fun setSystemNotificationsEnabled(systemNotificationsEnabled: Boolean) {
        _systemNotificationsEnabled.emit(systemNotificationsEnabled)
        settings[SettingsRepository::systemNotificationsEnabled.name] = systemNotificationsEnabled
    }

    override suspend fun setArchivedGamesDisableUpdateChecks(archivedGamesDisableUpdateChecks: Boolean) {
        _archivedGamesDisableUpdateChecks.emit(archivedGamesDisableUpdateChecks)
        settings[SettingsRepository::archivedGamesDisableUpdateChecks.name] = archivedGamesDisableUpdateChecks
    }

    override suspend fun setDateFormat(dateFormat: String) {
        _dateFormat.emit(dateFormat)
        settings[SettingsRepository::dateFormat.name] = dateFormat
    }

    override suspend fun setTimeFormat(timeFormat: String) {
        _timeFormat.emit(timeFormat)
        settings[SettingsRepository::timeFormat.name] = timeFormat
    }

    override suspend fun setGridColumns(gridColumns: GridColumns) {
        _gridColumns.emit(gridColumns)
        settings[SettingsRepository::gridColumns.name] = gridColumns.name
    }

    override suspend fun setGridImageAspectRatio(gridImageAspectRatio: Float) {
        _gridImageAspectRatio.emit(gridImageAspectRatio)
        settings[SettingsRepository::gridImageAspectRatio.name] = gridImageAspectRatio
    }

    override suspend fun setGamesDir(gamesDir: String) {
        _gamesDir.emit(gamesDir)
        settings[SettingsRepository::gamesDir.name] = gamesDir
    }
}

internal expect class SettingsRepositoryImpl : SettingsRepositoryShared
