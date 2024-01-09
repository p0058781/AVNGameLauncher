package org.skynetsoftware.avnlauncher.launcher

import android.app.Application
import android.content.Intent
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.logger.Logger
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter

actual val gameLauncherKoinModule = module {
    single<GameLauncher> {
        GameLauncherAndroid(get(), get(), androidApplication())
    }
}

private class GameLauncherAndroid(
    private val logger: Logger,
    private val eventCenter: EventCenter,
    private val application: Application,
) : GameLauncher {
    override fun launch(
        game: Game,
        executablePath: String,
    ) {
        val launchIntent: Intent? = application.packageManager.getLaunchIntentForPackage(executablePath)
        if (launchIntent != null) {
            application.startActivity(launchIntent)
            logger.warning("App started. Tracking play time is impossible on Android")
        } else {
            eventCenter.emit(Event.ToastMessage(MR.strings.gameLauncherInvalidExecutableToast, game.title))
        }
    }
}
