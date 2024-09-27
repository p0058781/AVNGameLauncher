package org.skynetsoftware.avnlauncher.launcher

import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.domain.model.Game

expect val gameLauncherKoinModule: Module

interface GameLauncher {
    fun launch(
        game: Game,
        executablePath: String,
    )

    fun stop()
}
