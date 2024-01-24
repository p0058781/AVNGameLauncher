package org.skynetsoftware.avnlauncher.data.repository

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.domain.utils.Option

internal actual fun Module.settingsKoinModule() {
    single<SettingsRepository> { SettingsRepositoryImpl(Settings(), get()) }
}

internal actual class SettingsRepositoryImpl(settings: Settings, configManager: ConfigManager) :
    SettingsRepositoryShared(settings, configManager) {
    override val gamesDir: Option<out StateFlow<String?>> = Option.None()

    override suspend fun setGamesDir(gamesDir: String) {
        // no-op on android
    }
}
