package org.skynetsoftware.avnlauncher.ui.screen.settings

import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.domain.model.GridColumns
import org.skynetsoftware.avnlauncher.domain.model.LogLevel
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.ui.viewmodel.ShowToastViewModel

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    eventCenter: EventCenter,
) : ShowToastViewModel(eventCenter) {
    val periodicUpdateChecksEnabled = settingsRepository.periodicUpdateChecksEnabled
    val updateCheckInterval = settingsRepository.updateCheckInterval
    val gamesDir = settingsRepository.gamesDir
    val minimizeToTrayOnClose = settingsRepository.minimizeToTrayOnClose
    val startMinimized = settingsRepository.startMinimized
    val logLevel = settingsRepository.logLevel
    val showGifs = settingsRepository.showGifs
    val dateFormat = settingsRepository.dateFormat
    val timeFormat = settingsRepository.timeFormat
    val gridColumns = settingsRepository.gridColumns
    val systemNotificationsEnabled = settingsRepository.systemNotificationsEnabled
    val gridImageAspectRatio = settingsRepository.gridImageAspectRatio

    fun setPeriodicUpdateChecks(periodicUpdateChecks: Boolean) =
        viewModelScope.launch {
            settingsRepository.setPeriodicUpdateChecksEnabled(periodicUpdateChecks)
        }

    fun setUpdateCheckInterval(updateCheckInterval: Long) =
        viewModelScope.launch {
            settingsRepository.setUpdateCheckInterval(updateCheckInterval)
        }

    fun setGamesDir(gamesDir: String) =
        viewModelScope.launch {
            settingsRepository.setGamesDir(gamesDir)
        }

    fun setMinimizeToTrayOnClose(minimizeToTrayOnClose: Boolean) =
        viewModelScope.launch {
            settingsRepository.setMinimizeToTrayOnClose(minimizeToTrayOnClose)
        }

    fun setStartMinimized(startMinimized: Boolean) =
        viewModelScope.launch {
            settingsRepository.setStartMinimized(startMinimized)
        }

    fun setLogLevel(logLevel: LogLevel) =
        viewModelScope.launch {
            settingsRepository.setLogLevel(logLevel)
        }

    fun setShowGifs(showGifs: Boolean) =
        viewModelScope.launch {
            settingsRepository.setShowGifs(showGifs)
        }

    fun setDateFormat(dateFormat: String) =
        viewModelScope.launch {
            settingsRepository.setDateFormat(dateFormat)
        }

    fun setTimeFormat(timeFormat: String) =
        viewModelScope.launch {
            settingsRepository.setTimeFormat(timeFormat)
        }

    fun setGridColumns(gridColumns: GridColumns) =
        viewModelScope.launch {
            settingsRepository.setGridColumns(gridColumns)
        }

    fun setSystemNotificationsEnabled(systemNotificationsEnabled: Boolean) =
        viewModelScope.launch {
            settingsRepository.setSystemNotificationsEnabled(systemNotificationsEnabled)
        }

    fun setGridImageAspectRatio(gridImageAspectRatio: Float) =
        viewModelScope.launch {
            settingsRepository.setGridImageAspectRatio(gridImageAspectRatio)
        }
}