package org.skynetsoftware.avnlauncher.logging

internal actual class UncaughtExceptionHandlerLogger {
    actual fun init(logger: Logger) {
        logger.warning("UncaughtExceptionHandlerLogger for ios target is not implemented")
    }
}