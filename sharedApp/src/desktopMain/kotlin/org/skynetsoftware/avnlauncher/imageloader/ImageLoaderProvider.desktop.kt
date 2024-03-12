package org.skynetsoftware.avnlauncher.imageloader

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import kotlinx.coroutines.CoroutineDispatcher
import okio.Path.Companion.toPath
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.config.ConfigManager
import org.skynetsoftware.avnlauncher.logger.Logger

actual fun imageLoaderKoinModule(coroutineDispatcher: CoroutineDispatcher) =
    module {
        single<ImageLoader> { imageLoader(get(), get(), coroutineDispatcher) }
    }

private fun imageLoader(
    configManager: ConfigManager,
    avnLauncherLogger: Logger,
    coroutineDispatcher: CoroutineDispatcher,
) = ImageLoader(requestCoroutineContext = coroutineDispatcher) {
    options {
        playAnimate = false
    }
    logger = ImageLoaderLogger(avnLauncherLogger)
    components {
        setupDefaultComponents()
    }
    interceptor {
        memoryCacheConfig {
            maxSizePercent(MEMORY_CACHE_MAX_SIZE_PERCENT)
        }
        diskCacheConfig {
            directory(configManager.cacheDir.toPath().resolve("images"))
            maxSizeBytes(DISK_CACHE_MAX_SIZE_BYTES)
        }
    }
}
