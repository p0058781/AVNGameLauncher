package org.skynetsoftware.avnlauncher.settings

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.StateFlow
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.utils.Option

actual val settingsKoinModule = module {
    single<SettingsManager> { SettingsManagerImpl(Settings(), get()) }
}

actual class SettingsManagerImpl(settings: Settings, configManager: ConfigManager) :
    SettingsManagerShared(settings, configManager) {
    override val gamesDir: Option<out StateFlow<String?>> = Option.None()

    override suspend fun setGamesDir(gamesDir: String) {}
}
