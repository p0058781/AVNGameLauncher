package org.skynetsoftware.avnlauncher.imageloader

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.util.LogPriority
import okio.Path.Companion.toPath
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.Config
import org.skynetsoftware.avnlauncher.domain.coroutines.CoroutineDispatchers
import org.skynetsoftware.avnlauncher.logger.Logger

const val MEMORY_CACHE_MAX_SIZE_PERCENT = 0.25
const val DISK_CACHE_MAX_SIZE_BYTES = 512L * 1024 * 1024 // 512MB

fun imageLoaderKoinModule() =
    module {
        single<ImageLoaderFactory> { ImageLoaderFactoryImpl(get(), get(), get()) }
    }

interface ImageLoaderFactory {
    fun createImageLoader(animateGifs: Boolean): ImageLoader
}

private class ImageLoaderFactoryImpl(
    private val config: Config,
    private val avnLauncherLogger: Logger,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ImageLoaderFactory {
    override fun createImageLoader(animateGifs: Boolean): ImageLoader {
        return ImageLoader(requestCoroutineContext = coroutineDispatchers.io) {
            options {
                playAnimate = animateGifs
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
                    directory(config.cacheDir.toPath().resolve("images"))
                    maxSizeBytes(DISK_CACHE_MAX_SIZE_BYTES)
                }
            }
        }
    }
}

class ImageLoaderLogger(private val logger: Logger) : com.seiko.imageloader.util.Logger {
    override fun isLoggable(priority: LogPriority): Boolean {
        return true
    }

    override fun log(
        priority: LogPriority,
        tag: String,
        data: Any?,
        throwable: Throwable?,
        message: String,
    ) {
        when (priority) {
            LogPriority.VERBOSE -> logger.verbose(message)
            LogPriority.DEBUG -> logger.debug(message)
            LogPriority.INFO -> logger.info(message)
            LogPriority.WARN -> logger.warning(message)
            LogPriority.ERROR -> logger.error(message)
            LogPriority.ASSERT -> logger.error(message)
        }
    }
}
