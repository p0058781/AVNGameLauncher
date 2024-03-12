package org.skynetsoftware.avnlauncher.config

import okio.Path
import org.koin.core.module.Module

internal actual class ConfigManagerImpl() : ConfigManager {
    override val cacheDir: Config<Path>
        get() = TODO("Not yet implemented")

}

actual val configKoinModule: Module
    get() = TODO("Not yet implemented")