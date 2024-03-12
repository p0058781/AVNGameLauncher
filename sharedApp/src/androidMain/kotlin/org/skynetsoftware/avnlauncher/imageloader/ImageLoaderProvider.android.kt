package org.skynetsoftware.avnlauncher.imageloader

import android.content.Context
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import kotlinx.coroutines.CoroutineDispatcher
import okio.Path.Companion.toPath
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.logger.Logger

actual fun imageLoaderKoinModule(coroutineDispatcher: CoroutineDispatcher) =
    module {
        single<ImageLoader> { imageLoader(get(), get(), get(), coroutineDispatcher) }
    }

private fun imageLoader(
    context: Context,
    configManager: org.skynetsoftware.avnlauncher.config.ConfigManager,
    avnLauncherLogger: Logger,
    coroutineDispatcher: CoroutineDispatcher,
) = ImageLoader(requestCoroutineContext = coroutineDispatcher) {
    logger = ImageLoaderLogger(avnLauncherLogger)
    options {
        playAnimate = false
    }
    components {
        setupDefaultComponents(context)
    }
    interceptor {
        memoryCacheConfig {
            maxSizePercent(context, MEMORY_CACHE_MAX_SIZE_PERCENT)
        }
        diskCacheConfig {
            directory(configManager.cacheDir.toPath().resolve("images"))
            maxSizeBytes(DISK_CACHE_MAX_SIZE_BYTES)
        }
    }
}
