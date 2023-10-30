package org.skynetsoftware.avnlauncher.config

import android.content.Context
import okio.Path
import org.koin.dsl.module

actual val configKoinModule = module {
    single<ConfigManager> { ConfigManagerImpl(get()) }
}
internal actual class ConfigManagerImpl(context: Context): ConfigManager {
    override val cacheDir: Config<Path>
        get() = TODO("Not yet implemented")
}