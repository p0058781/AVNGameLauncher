package org.skynetsoftware.avnlauncher.config

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.utils.OS
import org.skynetsoftware.avnlauncher.utils.os
import java.io.File

private const val ApplicationName = "avnlauncher"

actual val configKoinModule = module {
    single<ConfigManager> { ConfigManagerImpl() }
}

private class ConfigManagerImpl : ConfigManager {

    private val cacheDirFile = when (os) {
        OS.Linux -> File(System.getProperty("user.home"), ".cache/$ApplicationName")
        OS.Windows -> File(System.getenv("AppData"), "$ApplicationName/cache")
        OS.Mac -> File(System.getProperty("user.home"), "Library/Caches/$ApplicationName")
    }

    private val dataDirFile = when (os) {
        OS.Linux -> File(System.getProperty("user.home"), ".config/$ApplicationName")
        OS.Windows -> File(System.getenv("AppData"), ApplicationName)
        OS.Mac -> File(System.getProperty("user.home"), "Library/Application Support/$ApplicationName")
    }

    override val cacheDir: String = cacheDirFile.absolutePath
    override val dataDir: String = dataDirFile.absolutePath
    override val remoteClientModeDefault: Boolean = false

    init {
        cacheDirFile.mkdirs()
        dataDirFile.mkdirs()
    }
}

actual interface ConfigManager : ConfigManagerShared