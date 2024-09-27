package org.skynetsoftware.avnlauncher.imageloader

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.util.Logger.Level
import okio.Path.Companion.toPath
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.Config
import org.skynetsoftware.avnlauncher.logger.Logger

const val MEMORY_CACHE_MAX_SIZE_BYTES = 256 * 1024 * 1024L // 256 MB
const val DISK_CACHE_MAX_SIZE_BYTES = 1024L * 1024 * 1024L // 1 GB

actual fun imageLoaderKoinModule() =
    module {
        single<ImageLoaderFactory> { ImageLoaderFactoryImpl(get(), get()) }
    }

private class ImageLoaderFactoryImpl(
    private val config: Config,
    private val avnLauncherLogger: Logger,
) : ImageLoaderFactory {
    override fun createImageLoader(
        animateGifs: Boolean,
        platformContext: PlatformContext,
    ): ImageLoader {
        return ImageLoader.Builder(platformContext)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizeBytes(MEMORY_CACHE_MAX_SIZE_BYTES)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(config.cacheDir.toPath().resolve("images"))
                    .maxSizeBytes(DISK_CACHE_MAX_SIZE_BYTES)
                    .build()
            }
            .logger(ImageLoaderLogger(avnLauncherLogger))
            .build()
    }
}

class ImageLoaderLogger(private val logger: Logger) : coil3.util.Logger {
    override var minLevel: Level = Level.Verbose

    override fun log(
        tag: String,
        level: Level,
        message: String?,
        throwable: Throwable?,
    ) {
        when (level) {
            Level.Verbose -> message?.let { logger.verbose(it) }
            Level.Debug -> message?.let { logger.debug(it) }
            Level.Info -> message?.let { logger.info(it) }
            Level.Warn -> message?.let { logger.warning(it) }
            Level.Error -> {
                if (throwable != null) {
                    logger.error(throwable)
                } else if (message != null) {
                    logger.error(message)
                }
            }
        }
    }
}