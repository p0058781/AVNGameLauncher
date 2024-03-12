package org.skynetsoftware.avnlauncher.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.utils.Option

actual val settingsKoinModule = module {
    single<SettingsManager> { SettingsManagerImpl(Settings(), get()) }
}

actual class SettingsManagerImpl(private val settings: Settings, private val configManager: ConfigManager) :
    SettingsManagerShared(settings, configManager) {
    // TODO remove hardcoded value when settings screen is done
    private val _gamesDir = Option.Some(
        MutableStateFlow<String?>(
            settings.getString(
                SettingsManager::gamesDir.name,
                "/mnt/sata_4tb/AVN/Games",
            ),
        ),
    )
    override val gamesDir: Option<out StateFlow<String?>> get() = _gamesDir

    override suspend fun setGamesDir(gamesDir: String) {
        _gamesDir.value.emit(gamesDir)
        settings[SettingsManager::gamesDir.name] = gamesDir
    }
}
