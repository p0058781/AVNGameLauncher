package org.skynetsoftware.avnlauncher.launcher

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.settings.SettingsManager

actual val gameLauncherKoinModule = module {
    single<GameLauncher> {
        val configManager = get<ConfigManager>()
        if (configManager.remoteClientMode) {
            GameLauncherNoOp()
        } else {
            GameLauncherAndroid(get())
        }
    }
}

private class GameLauncherAndroid(private val settingsManager: SettingsManager) : GameLauncher {
    override fun launch(game: Game) {
        TODO("Not yet implemented")
    }
}
