package org.skynetsoftware.avnlauncher.config

import org.koin.core.module.Module

expect val configKoinModule: Module

interface ConfigManagerShared {
    val dataDir: String
    val cacheDir: String
    val remoteClientMode: Boolean
}

expect interface ConfigManager : ConfigManagerShared
