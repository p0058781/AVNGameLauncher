package org.skynetsoftware.avnlauncher.config

import okio.Path
import okio.Path.Companion.toOkioPath
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.utils.OS
import org.skynetsoftware.avnlauncher.utils.os
import java.io.File

private const val ApplicationName = "avnlauncher"

actual val configKoinModule = module {
    single<ConfigManager> { ConfigManagerImpl() }
}

internal actual class ConfigManagerImpl(): ConfigManager {

    private val cacheDirFile = when(os) {
        OS.Linux -> File(System.getProperty("user.home"), ".cache/$ApplicationName")
        OS.Windows -> File(System.getenv("AppData"), "$ApplicationName/cache")
        OS.Mac -> File(System.getProperty("user.home"), "Library/Caches/$ApplicationName")
    }

    override val cacheDir: Config<Path> = Config.Some(cacheDirFile.toOkioPath())

    init {
        cacheDirFile.mkdirs()
    }
}