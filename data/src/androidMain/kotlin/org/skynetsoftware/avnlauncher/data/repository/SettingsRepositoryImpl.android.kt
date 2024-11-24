package org.skynetsoftware.avnlauncher.data.repository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.domain.repository.ISettingsDefaults
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.domain.utils.MutableStateFlow
import org.skynetsoftware.avnlauncher.domain.utils.Option

internal actual fun Module.settingsKoinModule() {
    single { Settings() }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}

internal actual class SettingsRepositoryImpl(private val settings: Settings) :
    SettingsRepositoryShared(settings) {
    override val minimizeToTrayOnClose = Option.None<StateFlow<Boolean>>()

    override suspend fun setMinimizeToTrayOnClose(minimizeToTrayOnClose: Boolean) {
        // no-op on android
    }

    override val startMinimized = Option.None<StateFlow<Boolean>>()

    override val httpServerEnabled: Option<out StateFlow<Boolean>> = Option.None()

    private val _showGifs = Option.Some(
        MutableStateFlow {
            settings.getBoolean(
                SettingsRepository::showGifs.name,
                SettingsDefaults.showGifs,
            )
        },
    )
    override val showGifs: Option<out StateFlow<Boolean>> get() = _showGifs

    override suspend fun setStartMinimized(startMinimized: Boolean) {
        // no-op on android
    }

    override suspend fun setHttpServerEnabled(httpServerEnabled: Boolean) {
        // no-op on android
    }

    override suspend fun setShowGifs(showGifs: Boolean) {
        _showGifs.value.emit(showGifs)
        settings[SettingsRepository::showGifs.name] = showGifs
    }
}

actual object SettingsDefaults : ISettingsDefaults() {
    override val sfwModeEnabled = true
    const val showGifs = true
}
