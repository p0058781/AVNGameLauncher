package org.skynetsoftware.avnlauncher.ui.screen.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    val periodicUpdateChecksEnabled = settingsRepository.periodicUpdateChecksEnabled
    val gamesDir = settingsRepository.gamesDir
    val minimizeToTrayOnClose = settingsRepository.minimizeToTrayOnClose

    fun setPeriodicUpdateChecks(periodicUpdateChecks: Boolean) =
        viewModelScope.launch {
            settingsRepository.setPeriodicUpdateChecksEnabled(periodicUpdateChecks)
        }

    fun setGamesDir(gamesDir: String) =
        viewModelScope.launch {
            settingsRepository.setGamesDir(gamesDir)
        }

    fun setMinimizeToTrayOnClose(minimizeToTrayOnClose: Boolean) =
        viewModelScope.launch {
            settingsRepository.setMinimizeToTrayOnClose(minimizeToTrayOnClose)
        }
}
