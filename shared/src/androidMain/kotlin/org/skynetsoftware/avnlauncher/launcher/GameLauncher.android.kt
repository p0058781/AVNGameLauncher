package org.skynetsoftware.avnlauncher.launcher

import android.app.Application
import android.content.Intent
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.logging.Logger
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.utils.format

actual val gameLauncherKoinModule = module {
    single<GameLauncher> {
        val configManager = get<ConfigManager>()
        if (configManager.remoteClientMode) {
            GameLauncherNoOp()
        } else {
            GameLauncherAndroid(get(), get(), androidApplication())
        }
    }
}

private class GameLauncherAndroid(
    private val logger: Logger,
    private val eventCenter: EventCenter,
    private val application: Application,
) : GameLauncher {
    override fun launch(game: Game) {
        val packageName = game.executablePath
        if (packageName.isNullOrBlank()) {
            val message = R.strings.gameLauncherInvalidExecutableToast.format(game.title)
            logger.info(message)
            eventCenter.emit(Event.ToastMessage(message))
            return
        }
        val launchIntent: Intent? = application.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            application.startActivity(launchIntent)
            logger.warning("App started. Tracking play time is impossible on Android")
        } else {
            val message = R.strings.gameLauncherInvalidExecutableToast.format(game.title)
            logger.info(message)
            eventCenter.emit(Event.ToastMessage(message))
        }
    }
}
