package org.skynetsoftware.avnlauncher.logging

actual fun logUncaughtExceptions(logger: Logger) {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        logger.error(e)
    }
}
