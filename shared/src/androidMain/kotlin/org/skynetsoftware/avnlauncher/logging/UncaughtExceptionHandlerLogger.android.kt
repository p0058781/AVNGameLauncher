package org.skynetsoftware.avnlauncher.logging

internal actual class UncaughtExceptionHandlerLogger {
    actual fun init(logger: Logger) {
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            logger.error(e)
        }
    }
}