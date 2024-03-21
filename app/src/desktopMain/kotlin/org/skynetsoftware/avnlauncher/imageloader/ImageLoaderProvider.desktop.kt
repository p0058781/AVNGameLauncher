package org.skynetsoftware.avnlauncher.imageloader

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import kotlinx.coroutines.CoroutineDispatcher
import okio.Path.Companion.toPath
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.Config
import org.skynetsoftware.avnlauncher.logger.Logger

actual fun imageLoaderKoinModule(coroutineDispatcher: CoroutineDispatcher) =
    module {
        single<ImageLoaderFactory> { ImageLoaderFactoryImpl(get(), get(), coroutineDispatcher) }
    }

private class ImageLoaderFactoryImpl(
    private val config: Config,
    private val avnLauncherLogger: Logger,
    private val coroutineDispatcher: CoroutineDispatcher,
) : ImageLoaderFactory {
    override fun createImageLoader(animateGifs: Boolean): ImageLoader {
        return ImageLoader(requestCoroutineContext = coroutineDispatcher) {
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
