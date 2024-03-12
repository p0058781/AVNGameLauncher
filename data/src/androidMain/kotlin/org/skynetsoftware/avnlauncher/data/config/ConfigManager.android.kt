package org.skynetsoftware.avnlauncher.data.config

import android.content.Context
import org.koin.core.module.Module

internal actual fun Module.configKoinModule() {
    single<ConfigManager> { ConfigManagerImpl(get()) }
}

private class ConfigManagerImpl(context: Context) : ConfigManager() {
    override val cacheDir: String = context.cacheDir.absolutePath
    override val dataDir: String = context.dataDir.absolutePath
    override val sfwModeEnabledDefault: Boolean = true
    override val periodicUpdateChecksDefault: Boolean = false
}

actual abstract class ConfigManager : ConfigManagerShared()
