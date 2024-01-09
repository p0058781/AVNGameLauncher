package org.skynetsoftware.avnlauncher.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.settings.SettingsManager

class SettingsScreenModel(private val settingsManager: SettingsManager) : ScreenModel {
    val syncEnabled = settingsManager.syncEnabled
    val fastUpdateCheck = settingsManager.fastUpdateCheck
    val forceDarkTheme = settingsManager.forceDarkTheme
    val gamesDir = settingsManager.gamesDir

    fun setSyncEnabled(syncEnabled: Boolean) =
        screenModelScope.launch {
            settingsManager.setSyncEnabled(syncEnabled)
        }

    fun setFastUpdateCheck(fastUpdateCheck: Boolean) =
        screenModelScope.launch {
            settingsManager.setFastUpdateCheck(fastUpdateCheck)
        }

    fun setForceDarkTheme(forceDarkTheme: Boolean) =
        screenModelScope.launch {
            settingsManager.setForceDarkTheme(forceDarkTheme)
        }

    fun setGamesDir(gamesDir: String) =
        screenModelScope.launch {
            settingsManager.setGamesDir(gamesDir)
        }
}
