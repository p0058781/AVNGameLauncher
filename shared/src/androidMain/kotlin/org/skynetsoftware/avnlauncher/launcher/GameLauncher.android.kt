package org.skynetsoftware.avnlauncher.launcher

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.settings.SettingsManager

actual val gameLauncherKoinModule = module {
    single<GameLauncher> { GameLauncherAndroid(get()) }
}

private class GameLauncherAndroid(private val settingsManager: SettingsManager) : GameLauncher {
    override fun launch(game: Game) {
        if (settingsManager.remoteClientMode.value) {
            return
        }
        TODO("Not yet implemented")
    }

}