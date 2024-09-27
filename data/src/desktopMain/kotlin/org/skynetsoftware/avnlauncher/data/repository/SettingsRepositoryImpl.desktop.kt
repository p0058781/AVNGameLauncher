package org.skynetsoftware.avnlauncher.data.repository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.domain.repository.ISettingsDefaults
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.domain.utils.MutableStateFlow
import org.skynetsoftware.avnlauncher.domain.utils.Option

@Suppress("MayBeConst")
actual object SettingsDefaults : ISettingsDefaults() {
    val minimizeToTrayOnClose: Boolean = false
    val startMinimized: Boolean = false
    val httpServerEnabled: Boolean = false
}

internal actual fun Module.settingsKoinModule() {
    single { Settings() }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}

internal actual class SettingsRepositoryImpl(private val settings: Settings) :
    SettingsRepositoryShared(settings) {

    private val _minimizeToTrayOnClose = Option.Some(
        MutableStateFlow {
            settings.getBoolean(
                SettingsRepository::minimizeToTrayOnClose.name,
                SettingsDefaults.minimizeToTrayOnClose,
            )
        },
    )
    override val minimizeToTrayOnClose: Option<out StateFlow<Boolean>> get() = _minimizeToTrayOnClose

    private val _startMinimized = Option.Some(
        MutableStateFlow {
            settings.getBoolean(
                SettingsRepository::startMinimized.name,
                SettingsDefaults.startMinimized,
            )
        },
    )
    override val startMinimized: Option<out StateFlow<Boolean>> get() = _startMinimized

    private val _httpServerEnabled = Option.Some(
        MutableStateFlow {
            settings.getBoolean(
                SettingsRepository::httpServerEnabled.name,
                SettingsDefaults.httpServerEnabled,
            )
        },
    )
    override val httpServerEnabled: Option<out StateFlow<Boolean>> get() = _httpServerEnabled

    override suspend fun setMinimizeToTrayOnClose(minimizeToTrayOnClose: Boolean) {
        _minimizeToTrayOnClose.value.emit(minimizeToTrayOnClose)
        settings[SettingsRepository::minimizeToTrayOnClose.name] = minimizeToTrayOnClose
    }

    override suspend fun setStartMinimized(startMinimized: Boolean) {
        _startMinimized.value.emit(startMinimized)
        settings[SettingsRepository::startMinimized.name] = startMinimized
    }

    override suspend fun setHttpServerEnabled(httpServerEnabled: Boolean) {
        _httpServerEnabled.value.emit(httpServerEnabled)
        settings[SettingsRepository::httpServerEnabled.name] = httpServerEnabled
    }
}
