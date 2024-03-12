package org.skynetsoftware.avnlauncher.config

import android.content.Context
import okio.Path
import okio.Path.Companion.toOkioPath
import org.koin.dsl.module
import java.io.File
import java.text.SimpleDateFormat

actual val configKoinModule = module {
    single<ConfigManager> { ConfigManagerImpl(get())
    SimpleDateFormat}
}
internal actual class ConfigManagerImpl(context: Context): ConfigManager {
    override val cacheDir: Config<Path> = Config.Some(File(context.cacheDir, "images").toOkioPath())
}