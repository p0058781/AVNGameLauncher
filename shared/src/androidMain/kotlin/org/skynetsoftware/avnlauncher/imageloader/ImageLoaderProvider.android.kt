package org.skynetsoftware.avnlauncher.imageloader

import android.content.Context
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toPath
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.logging.Logger

actual val imageLoaderKoinModule = module {
    single<ImageLoader> { imageLoader(get(), get(), get()) }
}

private fun imageLoader(
    context: Context,
    configManager: ConfigManager,
    avnLauncherLogger: Logger,
) = ImageLoader(requestCoroutineContext = Dispatchers.IO) {
    // logger = DebugLogger(LogPriority.WARN)
    // TODO use avnLauncherLogger
    options {
        playAnimate = false
    }
    components {
        setupDefaultComponents(context)
    }
    interceptor {
        memoryCacheConfig {
            maxSizePercent(context, 0.25)
        }
        diskCacheConfig {
            directory(configManager.cacheDir.toPath().resolve("images"))
            maxSizeBytes(512L * 1024 * 1024) // 512MB
        }
    }
}
