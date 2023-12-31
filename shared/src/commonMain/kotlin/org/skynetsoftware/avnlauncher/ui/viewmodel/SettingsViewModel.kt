package org.skynetsoftware.avnlauncher.ui.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.settings.SettingsManager

class SettingsViewModel(private val settingsManager: SettingsManager) : ViewModel() {
    val syncEnabled = settingsManager.syncEnabled
    val fastUpdateCheck = settingsManager.fastUpdateCheck
    val forceDarkTheme = settingsManager.forceDarkTheme
    val gamesDir = settingsManager.gamesDir

    fun setSyncEnabled(syncEnabled: Boolean) =
        viewModelScope.launch {
            settingsManager.setSyncEnabled(syncEnabled)
        }

    fun setFastUpdateCheck(fastUpdateCheck: Boolean) =
        viewModelScope.launch {
            settingsManager.setFastUpdateCheck(fastUpdateCheck)
        }

    fun setForceDarkTheme(forceDarkTheme: Boolean) =
        viewModelScope.launch {
            settingsManager.setForceDarkTheme(forceDarkTheme)
        }

    fun setGamesDir(gamesDir: String) =
        viewModelScope.launch {
            settingsManager.setGamesDir(gamesDir)
        }
}
