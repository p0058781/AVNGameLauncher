package org.skynetsoftware.avnlauncher.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository

class SettingsScreenModel(private val settingsRepository: SettingsRepository) : ScreenModel {
    val periodicUpdateChecksEnabled = settingsRepository.periodicUpdateChecksEnabled
    val gamesDir = settingsRepository.gamesDir
    val minimizeToTrayOnClose = settingsRepository.minimizeToTrayOnClose

    fun setPeriodicUpdateChecks(periodicUpdateChecks: Boolean) =
        screenModelScope.launch {
            settingsRepository.setPeriodicUpdateChecksEnabled(periodicUpdateChecks)
        }

    fun setGamesDir(gamesDir: String) =
        screenModelScope.launch {
            settingsRepository.setGamesDir(gamesDir)
        }

    fun setMinimizeToTrayOnClose(minimizeToTrayOnClose: Boolean) =
        screenModelScope.launch {
            settingsRepository.setMinimizeToTrayOnClose(minimizeToTrayOnClose)
        }
}
