package org.skynetsoftware.avnlauncher.config

import android.content.Context
import okio.Path
import okio.Path.Companion.toOkioPath
import org.koin.dsl.module
import java.io.File
import java.text.SimpleDateFormat

actual val configKoinModule = module {
    single<ConfigManager> { ConfigManagerImpl(get()) }
}
private class ConfigManagerImpl(context: Context): ConfigManager {
    override val cacheDir: String = context.cacheDir.absolutePath
    override val dataDir: String = context.dataDir.absolutePath
    override val remoteClientModeDefault: Boolean = true
}

actual interface ConfigManager : ConfigManagerShared