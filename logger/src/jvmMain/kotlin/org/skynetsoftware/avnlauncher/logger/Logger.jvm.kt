package org.skynetsoftware.avnlauncher.logger

actual fun logUncaughtExceptions(logger: Logger) {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        logger.error(e)
    }
}
