package org.skynetsoftware.avnlauncher.imageloader

import android.content.Context
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import okio.Path.Companion.toPath
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.Config
import org.skynetsoftware.avnlauncher.domain.coroutines.CoroutineDispatchers
import org.skynetsoftware.avnlauncher.logger.Logger

actual fun imageLoaderKoinModule() =
    module {
        single<ImageLoaderFactory> { ImageLoaderFactoryImpl(get(), get(), get(), get()) }
    }

private class ImageLoaderFactoryImpl(
    private val context: Context,
    private val config: Config,
    private val avnLauncherLogger: Logger,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ImageLoaderFactory {
    override fun createImageLoader(animateGifs: Boolean): ImageLoader {
        return ImageLoader(requestCoroutineContext = coroutineDispatchers.io) {
            logger = ImageLoaderLogger(avnLauncherLogger)
            options {
                playAnimate = animateGifs
            }
            components {
                setupDefaultComponents(context)
            }
            interceptor {
                memoryCacheConfig {
                    maxSizePercent(context, MEMORY_CACHE_MAX_SIZE_PERCENT)
                }
                diskCacheConfig {
                    directory(config.cacheDir.toPath().resolve("images"))
                    maxSizeBytes(DISK_CACHE_MAX_SIZE_BYTES)
                }
            }
        }
    }
}
