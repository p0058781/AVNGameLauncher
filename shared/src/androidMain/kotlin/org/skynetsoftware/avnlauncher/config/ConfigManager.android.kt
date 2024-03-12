package org.skynetsoftware.avnlauncher.config

import android.content.Context
import org.koin.dsl.module

actual val configKoinModule = module {
    single<ConfigManager> { ConfigManagerImpl(get()) }
}

private class ConfigManagerImpl(context: Context) : ConfigManager {
    override val cacheDir: String = context.cacheDir.absolutePath
    override val dataDir: String = context.dataDir.absolutePath
    override val remoteClientMode: Boolean = true
}

actual interface ConfigManager : ConfigManagerShared
