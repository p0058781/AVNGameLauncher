package org.skynetsoftware.avnlauncher.config

import okio.Path
import org.koin.dsl.module

actual val configKoinModule = module {
    single<ConfigManager> { ConfigManagerImpl() }
}

internal actual class ConfigManagerImpl() : ConfigManager {

    override val cacheDir: Config<Path> = TODO("not implemented")
}
