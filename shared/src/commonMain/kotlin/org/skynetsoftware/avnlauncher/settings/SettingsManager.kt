package org.skynetsoftware.avnlauncher.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.repository.Filter
import org.skynetsoftware.avnlauncher.data.repository.SortDirection
import org.skynetsoftware.avnlauncher.data.repository.SortOrder

val settingsKoinModule = module {
    single<SettingsManager> { SettingsManagerImpl(Settings(), get()) }
}

interface SettingsManager {
    val selectedFilter: StateFlow<Filter>
    val selectedSortOrder: StateFlow<SortOrder>
    val selectedSortDirection: StateFlow<SortDirection>
    val fastUpdateCheck: StateFlow<Boolean>
    val gamesDir: StateFlow<String?>
    val lastSyncTime: StateFlow<Long>
    val syncEnabled: StateFlow<Boolean>

    suspend fun setSelectedFilter(filter: Filter)

    suspend fun setSelectedSortOrder(sortOrder: SortOrder)

    suspend fun setSelectedSortDirection(sortDirection: SortDirection)

    suspend fun setFastUpdateCheck(fastUpdateCheck: Boolean)

    suspend fun setGamesDir(gamesDir: String)

    suspend fun setLastSyncTime(lastSyncTime: Long)

    suspend fun setSyncEnabled(syncEnabled: Boolean)
}

class SettingsManagerImpl(
    private val settings: Settings,
    private val configManager: ConfigManager,
) : SettingsManager {
    private val _selectedFilter = MutableStateFlow {
        val selectedFilterClassname =
            settings.getString(
                SettingsManager::selectedFilter.name,
                configManager.selectedFilterDefault::class.simpleName!!,
            )
        Filter.entries.find { it::class.simpleName == selectedFilterClassname } ?: configManager.selectedFilterDefault
    }
    override val selectedFilter: StateFlow<Filter> get() = _selectedFilter

    private val _selectedSortOrder = MutableStateFlow {
        val selectedSortOrderClassname = settings.getString(
            SettingsManager::selectedSortOrder.name,
            configManager.selectedSortOrderDefault::class.simpleName!!,
        )
        SortOrder.entries.find { it::class.simpleName == selectedSortOrderClassname }
            ?: configManager.selectedSortOrderDefault
    }
    override val selectedSortOrder: StateFlow<SortOrder> get() = _selectedSortOrder

    private val _selectedSortDirection = MutableStateFlow {
        val selectedSortDirectionClassname =
            settings.getString(
                SettingsManager::selectedSortDirection.name,
                configManager.selectedSortOrderDirectionDefault.name,
            )
        SortDirection.entries.find { it.name == selectedSortDirectionClassname }
            ?: configManager.selectedSortOrderDirectionDefault
    }
    override val selectedSortDirection: StateFlow<SortDirection> get() = _selectedSortDirection

    private val _fastUpdateCheck = MutableStateFlow(
        settings.getBoolean(
            SettingsManager::fastUpdateCheck.name,
            configManager.fastUpdateCheckDefault,
        ),
    )
    override val fastUpdateCheck: StateFlow<Boolean> get() = _fastUpdateCheck

    // TODO remove hardcoded value when settings screen is done
    private val _gamesDir =
        MutableStateFlow(settings.getString(SettingsManager::gamesDir.name, "/mnt/sata_4tb/AVN/Games"))
    override val gamesDir: StateFlow<String?> get() = _gamesDir

    private val _lastSyncTime =
        MutableStateFlow(settings.getLong(SettingsManager::lastSyncTime.name, 0L))
    override val lastSyncTime: StateFlow<Long> get() = _lastSyncTime

    private val _syncEnabled =
        MutableStateFlow(settings.getBoolean(SettingsManager::syncEnabled.name, configManager.syncEnabled))
    override val syncEnabled: StateFlow<Boolean> get() = _syncEnabled

    override suspend fun setSelectedFilter(filter: Filter) {
        _selectedFilter.emit(filter)
        settings[SettingsManager::selectedFilter.name] = filter::class.simpleName
    }

    override suspend fun setSelectedSortOrder(sortOrder: SortOrder) {
        _selectedSortOrder.emit(sortOrder)
        settings[SettingsManager::selectedSortOrder.name] = sortOrder::class.simpleName
    }

    override suspend fun setSelectedSortDirection(sortDirection: SortDirection) {
        _selectedSortDirection.emit(sortDirection)
        settings[SettingsManager::selectedSortDirection.name] = sortDirection.name
    }

    override suspend fun setFastUpdateCheck(fastUpdateCheck: Boolean) {
        _fastUpdateCheck.emit(fastUpdateCheck)
        settings[SettingsManager::fastUpdateCheck.name] = fastUpdateCheck
    }

    override suspend fun setGamesDir(gamesDir: String) {
        _gamesDir.emit(gamesDir)
        settings[SettingsManager::gamesDir.name] = gamesDir
    }

    override suspend fun setLastSyncTime(lastSyncTime: Long) {
        _lastSyncTime.emit(lastSyncTime)
        settings[SettingsManager::lastSyncTime.name] = lastSyncTime
    }

    override suspend fun setSyncEnabled(syncEnabled: Boolean) {
        _syncEnabled.emit(syncEnabled)
        settings[SettingsManager::syncEnabled.name] = syncEnabled
    }

    private fun <T> MutableStateFlow(initialValue: () -> T): MutableStateFlow<T> {
        return MutableStateFlow(initialValue())
    }
}

// TODO [medium] import game executable search location for desktop
