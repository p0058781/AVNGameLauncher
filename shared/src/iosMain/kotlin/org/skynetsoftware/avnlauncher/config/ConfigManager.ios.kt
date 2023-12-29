package org.skynetsoftware.avnlauncher.config

import org.koin.dsl.module

actual val configKoinModule = module {
    single<ConfigManager> { ConfigManagerImpl() }
}

private class ConfigManagerImpl : ConfigManager() {
    override val dataDir: String
        get() = TODO("Not yet implemented")
    override val cacheDir: String
        get() = TODO("Not yet implemented")
    override val remoteClientMode: Boolean = true
    override val syncEnabledDefault: Boolean = false
    override val sfwModeEnabledDefault: Boolean = true
}

actual abstract class ConfigManager : ConfigManagerShared()
