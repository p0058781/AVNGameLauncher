package org.skynetsoftware.avnlauncher.imageloader

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.util.DebugLogger
import com.seiko.imageloader.util.LogPriority
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toPath
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.logging.Logger

actual val imageLoaderKoinModule = module {
    single<ImageLoader> { imageLoader(get(), get()) }
}

private fun imageLoader(configManager: ConfigManager, avnLauncherLogger: Logger) =
    ImageLoader(requestCoroutineContext = Dispatchers.IO) {
        logger = DebugLogger(LogPriority.WARN)
        //TODO use avnLauncherLogger
        components {
            setupDefaultComponents()
        }
        interceptor {
            memoryCacheConfig {
                maxSizePercent(0.25)
            }
            diskCacheConfig {
                directory(configManager.cacheDir.toPath().resolve("images"))
                maxSizeBytes(512L * 1024 * 1024) // 512MB
            }
        }
    }