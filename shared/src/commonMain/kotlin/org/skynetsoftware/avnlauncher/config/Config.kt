package org.skynetsoftware.avnlauncher.config

import okio.Path
import org.koin.core.module.Module

expect val configKoinModule: Module

sealed class Config<T> {
    class None<T> : Config<T>()
    class Some<T>(val value: T) : Config<T>()
}

interface ConfigManager {
    val cacheDir: Config<Path>
}

internal expect class ConfigManagerImpl : ConfigManager

