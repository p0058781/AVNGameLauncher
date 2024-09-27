package org.skynetsoftware.avnlauncher.logger

import org.koin.core.module.Module
import kotlin.system.exitProcess

fun logUncaughtExceptions(logger: Logger) {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        logger.error(e)
        exitProcess(-1)
    }
}

expect val loggerKoinModule: Module

interface Logger {
    fun verbose(message: String)

    fun debug(message: String)

    fun info(message: String)

    fun warning(message: String)

    fun error(message: String)

    fun error(throwable: Throwable)
}

internal fun Logger.getLoggerName(): String {
    // iterate stacktrace until we find first occurrence of LoggerImpl
    // then find first next element that is not LoggerImpl
    val stacktrace = Thread.currentThread().stackTrace
    var stacktraceElement: StackTraceElement? = null
    var foundLogger = false
    for (element in stacktrace) {
        if (foundLogger) {
            if (element.className != this::class.qualifiedName) {
                stacktraceElement = element
                break
            }
        } else {
            if (element.className == this::class.qualifiedName) {
                foundLogger = true
            }
        }
    }
    return stacktraceElement?.className ?: "Logger"
}
