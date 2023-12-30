package org.skynetsoftware.avnlauncher.logging

import kotlinx.datetime.Clock
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.utils.SimpleDateFormat

expect fun logUncaughtExceptions(logger: Logger)

val loggerKoinModule = module {
    single<Logger> { LoggerImpl() }
}

interface Logger {
    fun info(message: String)

    fun warning(message: String)

    fun error(message: String)

    fun error(throwable: Throwable)
}

private class LoggerImpl : Logger {
    private val timeFormat = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss")

    override fun info(message: String) {
        log(Severity.Info, message)
    }

    override fun warning(message: String) {
        log(Severity.Warning, message)
    }

    override fun error(message: String) {
        log(Severity.Error, message)
    }

    override fun error(throwable: Throwable) {
        log(Severity.Error, throwable.stackTraceToString())
    }

    private fun log(
        severity: Severity,
        message: String,
    ) {
        val time = Clock.System.now()
        println("$severity[${timeFormat.format(time.toEpochMilliseconds())}]: $message")
    }
}
