package org.skynetsoftware.avnlauncher.data.repository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.GamesDisplayMode
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
    private val _selectedFilter = MutableStateFlow {
        val selectedFilterClassname =
            settings.getString(
                SettingsRepository::selectedFilter.name,
                SettingsDefaults.selectedFilter::class.simpleName!!,
            )
        Filter.entries.find { it::class.simpleName == selectedFilterClassname } ?: SettingsDefaults.selectedFilter
    }
    override val selectedFilter: StateFlow<Filter> get() = _selectedFilter

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
}

internal expect class SettingsRepositoryImpl : SettingsRepositoryShared
