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
    val remoteClientMode: StateFlow<Boolean>

    fun setSelectedFilter(filter: Filter)
    fun setSelectedSortOrder(sortOrder: SortOrder)
    fun setSelectedSortDirection(sortDirection: SortDirection)
    fun setFastUpdateCheck(fastUpdateCheck: Boolean)
    fun setGamesDir(gamesDir: String)
    fun setRemoteClientMode(remoteClientMode: Boolean)
}

class SettingsManagerImpl(private val settings: Settings, configManager: ConfigManager) : SettingsManager {
    private val _selectedFilter = MutableStateFlow {
        val selectedFilterClassname =
            settings.getString(SettingsManager::selectedFilter.name, Filter.All::class.simpleName ?: "All")
        Filter.entries.find { it::class.simpleName == selectedFilterClassname } ?: Filter.All
    }
    override val selectedFilter: StateFlow<Filter> get() = _selectedFilter

    private val _selectedSortOrder = MutableStateFlow {
        val selectedSortOrderClassname = settings.getString(
            SettingsManager::selectedSortOrder.name,
            SortOrder.LastPlayed::class.simpleName ?: "LastPlayed"
        )
        SortOrder.entries.find { it::class.simpleName == selectedSortOrderClassname } ?: SortOrder.LastPlayed
    }
    override val selectedSortOrder: StateFlow<SortOrder> get() = _selectedSortOrder

    private val _selectedSortDirection = MutableStateFlow {
        val selectedSortDirectionClassname =
            settings.getString(SettingsManager::selectedSortDirection.name, SortDirection.Descending.name)
        SortDirection.entries.find { it.name == selectedSortDirectionClassname } ?: SortDirection.Descending
    }
    override val selectedSortDirection: StateFlow<SortDirection> get() = _selectedSortDirection

    private val _fastUpdateCheck = MutableStateFlow(settings.getBoolean(SettingsManager::fastUpdateCheck.name, false))
    override val fastUpdateCheck: StateFlow<Boolean> get() = _fastUpdateCheck

    //TODO remove hardcoded value when settings screen is done
    private val _gamesDir =
        MutableStateFlow(settings.getString(SettingsManager::gamesDir.name, "/mnt/sata_4tb/AVN/Games"))
    override val gamesDir: StateFlow<String?> get() = _gamesDir

    private val _remoteClientMode = MutableStateFlow(
        settings.getBoolean(
            SettingsManager::remoteClientMode.name,
            configManager.remoteClientModeDefault
        )
    )
    override val remoteClientMode: StateFlow<Boolean> get() = _remoteClientMode

    override fun setSelectedFilter(filter: Filter) {
        _selectedFilter.tryEmit(filter)
        settings[SettingsManager::selectedFilter.name] = filter::class.simpleName
    }

    override fun setSelectedSortOrder(sortOrder: SortOrder) {
        _selectedSortOrder.tryEmit(sortOrder)
        settings[SettingsManager::selectedSortOrder.name] = sortOrder::class.simpleName
    }

    override fun setSelectedSortDirection(sortDirection: SortDirection) {
        _selectedSortDirection.tryEmit(sortDirection)
        settings[SettingsManager::selectedSortDirection.name] = sortDirection.name
    }

    override fun setFastUpdateCheck(fastUpdateCheck: Boolean) {
        _fastUpdateCheck.tryEmit(fastUpdateCheck)
        settings[SettingsManager::fastUpdateCheck.name] = fastUpdateCheck
    }

    override fun setGamesDir(gamesDir: String) {
        _gamesDir.tryEmit(gamesDir)
        settings[SettingsManager::gamesDir.name] = gamesDir
    }

    override fun setRemoteClientMode(remoteClientMode: Boolean) {
        _remoteClientMode.tryEmit(remoteClientMode)
        settings[SettingsManager::remoteClientMode.name] = remoteClientMode
    }

    private fun <T> MutableStateFlow(initialValue: () -> T): MutableStateFlow<T> {
        return MutableStateFlow(initialValue())
    }
}

//TODO [medium] import game executable search location for desktop