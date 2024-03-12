package org.skynetsoftware.avnlauncher.config

import okio.Path
import okio.Path.Companion.toOkioPath
import org.koin.dsl.module
import java.io.File

actual val configKoinModule = module {
    single<ConfigManager> { ConfigManagerImpl() }
}
internal actual class ConfigManagerImpl(): ConfigManager {

    private val rootDir = File(System.getProperty("user.home"), ".config${File.separator}avnlauncher")
    private val cacheDirFile = File(rootDir, ".cache")

    override val cacheDir: Config<Path> = Config.Some(cacheDirFile.toOkioPath())

    init {
        cacheDirFile.mkdirs()
    }
}