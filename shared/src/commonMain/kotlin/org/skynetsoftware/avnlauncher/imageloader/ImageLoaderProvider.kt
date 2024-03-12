package org.skynetsoftware.avnlauncher.imageloader

import com.seiko.imageloader.util.LogPriority
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.logging.Logger

expect val imageLoaderKoinModule: Module

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
