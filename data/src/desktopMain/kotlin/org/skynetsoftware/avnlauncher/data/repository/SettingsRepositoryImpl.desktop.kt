package org.skynetsoftware.avnlauncher.data.repository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.domain.repository.ISettingsDefaults
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.domain.utils.MutableStateFlow
import org.skynetsoftware.avnlauncher.domain.utils.Option

actual object SettingsDefaults : ISettingsDefaults()

internal actual fun Module.settingsKoinModule() {
    single { Settings() }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}

internal actual class SettingsRepositoryImpl(private val settings: Settings) :
    SettingsRepositoryShared(settings) {
    private val _gamesDir = Option.Some(
        MutableStateFlow {
            val value = settings.getString(
                SettingsRepository::gamesDir.name,
                "",
            )
            value.ifBlank {
                null
            }
        },
    )
    override val gamesDir: Option<out StateFlow<String?>> get() = _gamesDir

    private val _minimizeToTrayOnClose = Option.Some(
        MutableStateFlow {
            settings.getBoolean(
                SettingsRepository::minimizeToTrayOnClose.name,
                SettingsDefaults.minimizeToTrayOnClose,
            )
        },
    )
    override val minimizeToTrayOnClose: Option<out StateFlow<Boolean>> get() = _minimizeToTrayOnClose

    override suspend fun setGamesDir(gamesDir: String) {
        _gamesDir.value.emit(gamesDir)
        settings[SettingsRepository::gamesDir.name] = gamesDir
    }

    override suspend fun setMinimizeToTrayOnClose(minimizeToTrayOnClose: Boolean) {
        _minimizeToTrayOnClose.value.emit(minimizeToTrayOnClose)
        settings[SettingsRepository::minimizeToTrayOnClose.name] = minimizeToTrayOnClose
    }
}