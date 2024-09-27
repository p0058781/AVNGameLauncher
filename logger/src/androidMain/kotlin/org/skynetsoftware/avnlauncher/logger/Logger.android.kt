package org.skynetsoftware.avnlauncher.logger

import android.util.Log
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.domain.model.LogLevel
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository

actual val loggerKoinModule = module {
    single<Logger> { LoggerImpl(get()) }
}

private class LoggerImpl(
    private val settingsRepository: SettingsRepository,
) : Logger {
    private val logLevel: LogLevel
        get() = settingsRepository.logLevel.value

    override fun verbose(message: String) {
        if (logLevel.isAtLeast(LogLevel.Verbose)) {
            Log.v(getLoggerName(), message)
        }
    }

    override fun debug(message: String) {
        if (logLevel.isAtLeast(LogLevel.Debug)) {
            Log.d(getLoggerName(), message)
        }
    }

    override fun info(message: String) {
        if (logLevel.isAtLeast(LogLevel.Info)) {
            Log.i(getLoggerName(), message)
        }
    }

    override fun warning(message: String) {
        if (logLevel.isAtLeast(LogLevel.Warning)) {
            Log.w(getLoggerName(), message)
        }
    }

    override fun error(message: String) {
        Log.e(getLoggerName(), message)
    }

    override fun error(throwable: Throwable) {
        Log.e(getLoggerName(), throwable.message, throwable)
    }
}

fun LogLevel.isAtLeast(logLevel: LogLevel): Boolean {
    return this.ordinal >= logLevel.ordinal
}
