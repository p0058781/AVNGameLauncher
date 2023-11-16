package org.skynetsoftware.avnlauncher.config

import org.koin.core.module.Module

expect val configKoinModule: Module

interface ConfigManager {
    val dataDir: String
    val cacheDir: String
}

