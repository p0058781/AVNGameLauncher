package org.skynetsoftware.avnlauncher.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.StateFlow
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.utils.MutableStateFlow
import org.skynetsoftware.avnlauncher.utils.Option

actual val settingsKoinModule = module {
    single<SettingsManager> { SettingsManagerImpl(Settings(), get()) }
}

actual class SettingsManagerImpl(private val settings: Settings, configManager: ConfigManager) :
    SettingsManagerShared(settings, configManager) {
    private val _gamesDir = Option.Some(
        MutableStateFlow {
            val value = settings.getString(
                SettingsManager::gamesDir.name,
                "",
            )
            value.ifBlank {
                null
            }
        },
    )
    override val gamesDir: Option<out StateFlow<String?>> get() = _gamesDir

    override suspend fun setGamesDir(gamesDir: String) {
        _gamesDir.value.emit(gamesDir)
        settings[SettingsManager::gamesDir.name] = gamesDir
    }
}
