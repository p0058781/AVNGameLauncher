package org.skynetsoftware.avnlauncher.logger

import org.koin.core.module.Module

expect fun logUncaughtExceptions(logger: Logger)

expect val loggerKoinModule: Module

interface Logger {
    fun verbose(message: String)

    fun debug(message: String)

    fun info(message: String)

    fun warning(message: String)

    fun error(message: String)

    fun error(throwable: Throwable)

    enum class Severity {
        Verbose,
        Debug,
        Info,
        Warning,
        Error,
    }
}
