package org.skynetsoftware.avnlauncher.ui.viewmodel

import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.logging.Logger
import org.skynetsoftware.avnlauncher.settings.SettingsManager

actual suspend fun validateExecutables(
    gamesRepository: GamesRepository,
    settingsManager: SettingsManager,
    configManager: ConfigManager,
    logger: Logger,
) {
    TODO("not implemented")
}
