package org.skynetsoftware.avnlauncher.imageloader

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.util.LogPriority
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.logger.Logger

const val MEMORY_CACHE_MAX_SIZE_PERCENT = 0.25
const val DISK_CACHE_MAX_SIZE_BYTES = 512L * 1024 * 1024 // 512MB

expect fun imageLoaderKoinModule(coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO): Module

interface ImageLoaderFactory {
    fun createImageLoader(animateGifs: Boolean): ImageLoader
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
