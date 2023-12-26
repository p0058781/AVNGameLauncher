package org.skynetsoftware.avnlauncher.config

import org.koin.core.module.Module

expect val configKoinModule: Module

interface ConfigManagerShared {
    val dataDir: String
    val cacheDir: String
    val remoteClientModeDefault: Boolean
}

expect interface ConfigManager: ConfigManagerShared

