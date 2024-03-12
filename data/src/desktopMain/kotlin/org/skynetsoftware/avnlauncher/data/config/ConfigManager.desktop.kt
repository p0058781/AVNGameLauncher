package org.skynetsoftware.avnlauncher.data.config

import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.domain.utils.OS
import org.skynetsoftware.avnlauncher.domain.utils.os
import java.io.File

private const val APPLICATION_NAME = "avnlauncher"

internal actual fun Module.configKoinModule() {
    single<ConfigManager> { ConfigManagerImpl() }
}

private class ConfigManagerImpl : ConfigManager() {
    private val cacheDirFile = when (os) {
        OS.Linux -> File(System.getProperty("user.home"), ".cache/$APPLICATION_NAME")
        OS.Windows -> File(System.getenv("AppData"), "$APPLICATION_NAME/cache")
        OS.Mac -> File(System.getProperty("user.home"), "Library/Caches/$APPLICATION_NAME")
    }

    private val dataDirFile = when (os) {
        OS.Linux -> File(System.getProperty("user.home"), ".config/$APPLICATION_NAME")
        OS.Windows -> File(System.getenv("AppData"), APPLICATION_NAME)
        OS.Mac -> File(System.getProperty("user.home"), "Library/Application Support/$APPLICATION_NAME")
    }

    override val cacheDir: String = cacheDirFile.absolutePath
    override val dataDir: String = dataDirFile.absolutePath
    override val syncEnabledDefault: Boolean = true
    override val sfwModeEnabledDefault: Boolean = false

    init {
        cacheDirFile.mkdirs()
        dataDirFile.mkdirs()
    }
}

actual abstract class ConfigManager : ConfigManagerShared()
