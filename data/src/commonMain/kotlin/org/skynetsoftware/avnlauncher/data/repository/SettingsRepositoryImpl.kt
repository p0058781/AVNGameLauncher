package org.skynetsoftware.avnlauncher.data.repository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.domain.utils.MutableStateFlow

internal expect fun Module.settingsKoinModule()

abstract class SettingsRepositoryShared internal constructor(
    private val settings: Settings,
    private val configManager: ConfigManager,
) : SettingsRepository {
    private val _selectedFilter = MutableStateFlow {
        val selectedFilterClassname =
            settings.getString(
                SettingsRepository::selectedFilter.name,
                configManager.selectedFilterDefault::class.simpleName!!,
            )
        Filter.entries.find { it::class.simpleName == selectedFilterClassname } ?: configManager.selectedFilterDefault
    }
    override val selectedFilter: StateFlow<Filter> get() = _selectedFilter

    private val _selectedSortOrder = MutableStateFlow {
        val selectedSortOrderClassname = settings.getString(
            SettingsRepository::selectedSortOrder.name,
            configManager.selectedSortOrderDefault::class.simpleName!!,
        )
        SortOrder.entries.find { it::class.simpleName == selectedSortOrderClassname }
            ?: configManager.selectedSortOrderDefault
    }
    override val selectedSortOrder: StateFlow<SortOrder> get() = _selectedSortOrder

    private val _selectedSortDirection = MutableStateFlow {
        val selectedSortDirectionClassname =
            settings.getString(
                SettingsRepository::selectedSortDirection.name,
                configManager.selectedSortOrderDirectionDefault.name,
            )
        SortDirection.entries.find { it.name == selectedSortDirectionClassname }
            ?: configManager.selectedSortOrderDirectionDefault
    }
    override val selectedSortDirection: StateFlow<SortDirection> get() = _selectedSortDirection

    private val _fastUpdateCheck = MutableStateFlow {
        settings.getBoolean(
            SettingsRepository::fastUpdateCheck.name,
            configManager.fastUpdateCheckDefault,
        )
    }
    override val fastUpdateCheck: StateFlow<Boolean> get() = _fastUpdateCheck

    private val _lastSyncTime =
        MutableStateFlow { settings.getLong(SettingsRepository::lastSyncTime.name, 0L) }
    override val lastSyncTime: StateFlow<Long> get() = _lastSyncTime

    private val _sfwModeEnabled =
        MutableStateFlow {
            settings.getBoolean(
                SettingsRepository::sfwModeEnabled.name,
                configManager.sfwModeEnabledDefault,
            )
        }
    override val sfwModeEnabled: StateFlow<Boolean> get() = _sfwModeEnabled

    private val _forceDarkTheme =
        MutableStateFlow { settings.getBoolean(SettingsRepository::forceDarkTheme.name, false) }
    override val forceDarkTheme: StateFlow<Boolean> get() = _forceDarkTheme

    private val _periodicUpdateChecksEnabled =
        MutableStateFlow {
            settings.getBoolean(
                SettingsRepository::periodicUpdateChecksEnabled.name,
                configManager.periodicUpdateChecksDefault,
            )
        }
    override val periodicUpdateChecksEnabled: StateFlow<Boolean> get() = _periodicUpdateChecksEnabled

    override suspend fun setSelectedFilter(filter: Filter) {
        _selectedFilter.emit(filter)
        settings[SettingsRepository::selectedFilter.name] = filter::class.simpleName
    }

    override suspend fun setSelectedSortOrder(sortOrder: SortOrder) {
        _selectedSortOrder.emit(sortOrder)
        settings[SettingsRepository::selectedSortOrder.name] = sortOrder::class.simpleName
    }

    override suspend fun setSelectedSortDirection(sortDirection: SortDirection) {
        _selectedSortDirection.emit(sortDirection)
        settings[SettingsRepository::selectedSortDirection.name] = sortDirection.name
    }

    override suspend fun setFastUpdateCheck(fastUpdateCheck: Boolean) {
        _fastUpdateCheck.emit(fastUpdateCheck)
        settings[SettingsRepository::fastUpdateCheck.name] = fastUpdateCheck
    }

    override suspend fun setLastSyncTime(lastSyncTime: Long) {
        _lastSyncTime.emit(lastSyncTime)
        settings[SettingsRepository::lastSyncTime.name] = lastSyncTime
    }

    override suspend fun setSfwModeEnabled(sfwModeEnabled: Boolean) {
        _sfwModeEnabled.emit(sfwModeEnabled)
        settings[SettingsRepository::sfwModeEnabled.name] = sfwModeEnabled
    }

    override suspend fun setForceDarkTheme(forceDarkTheme: Boolean) {
        _forceDarkTheme.emit(forceDarkTheme)
        settings[SettingsRepository::forceDarkTheme.name] = forceDarkTheme
    }

    override suspend fun setPeriodicUpdateChecksEnabled(periodicUpdateChecksEnabled: Boolean) {
        _periodicUpdateChecksEnabled.emit(periodicUpdateChecksEnabled)
        settings[SettingsRepository::periodicUpdateChecksEnabled.name] = periodicUpdateChecksEnabled
    }
}

internal expect class SettingsRepositoryImpl : SettingsRepositoryShared
