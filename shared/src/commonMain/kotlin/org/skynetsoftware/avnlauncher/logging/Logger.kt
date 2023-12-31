package org.skynetsoftware.avnlauncher.logging

import kotlinx.datetime.Clock
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.utils.SimpleDateFormat

expect fun logUncaughtExceptions(logger: Logger)

val loggerKoinModule = module {
    single<Logger> { LoggerImpl(Logger.Severity.Info) }
}

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

private class LoggerImpl(private val severity: Logger.Severity) : Logger {
    private val timeFormat = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss")

    override fun verbose(message: String) {
        log(Logger.Severity.Verbose, message)
    }

    override fun debug(message: String) {
        log(Logger.Severity.Debug, message)
    }

    override fun info(message: String) {
        log(Logger.Severity.Info, message)
    }

    override fun warning(message: String) {
        log(Logger.Severity.Warning, message)
    }

    override fun error(message: String) {
        log(Logger.Severity.Error, message)
    }

    override fun error(throwable: Throwable) {
        log(Logger.Severity.Error, throwable.stackTraceToString())
    }

    private fun log(
        severity: Logger.Severity,
        message: String,
    ) {
        if (severity.ordinal >= this.severity.ordinal) {
            val time = Clock.System.now()
            println("$severity[${timeFormat.format(time.toEpochMilliseconds())}]: $message")
        }
    }
}
