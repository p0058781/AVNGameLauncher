package org.skynetsoftware.avnlauncher.logger

import kotlin.system.exitProcess

actual fun logUncaughtExceptions(logger: Logger) {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        logger.error(e)
        exitProcess(-1)
    }
}
