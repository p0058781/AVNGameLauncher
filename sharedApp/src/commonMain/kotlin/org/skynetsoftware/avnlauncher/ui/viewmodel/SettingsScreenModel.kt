package org.skynetsoftware.avnlauncher.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository

class SettingsScreenModel(private val settingsRepository: SettingsRepository) : ScreenModel {
    val fastUpdateCheck = settingsRepository.fastUpdateCheck
    val periodicUpdateChecksEnabled = settingsRepository.periodicUpdateChecksEnabled
    val gamesDir = settingsRepository.gamesDir

    fun setFastUpdateCheck(fastUpdateCheck: Boolean) =
        screenModelScope.launch {
            settingsRepository.setFastUpdateCheck(fastUpdateCheck)
        }

    fun setPeriodicUpdateChecks(periodicUpdateChecks: Boolean) =
        screenModelScope.launch {
            settingsRepository.setPeriodicUpdateChecksEnabled(periodicUpdateChecks)
        }

    fun setGamesDir(gamesDir: String) =
        screenModelScope.launch {
            settingsRepository.setGamesDir(gamesDir)
        }
}
