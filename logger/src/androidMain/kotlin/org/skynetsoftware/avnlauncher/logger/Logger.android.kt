package org.skynetsoftware.avnlauncher.logger

import android.util.Log
import org.koin.dsl.module

actual val loggerKoinModule = module {
    single<Logger> { LoggerImpl() }
}

private class LoggerImpl : Logger {
    override fun verbose(message: String) {
        Log.v(getLoggerName(), message)
    }

    override fun debug(message: String) {
        Log.d(getLoggerName(), message)
    }

    override fun info(message: String) {
        Log.i(getLoggerName(), message)
    }

    override fun warning(message: String) {
        Log.w(getLoggerName(), message)
    }

    override fun error(message: String) {
        Log.e(getLoggerName(), message)
    }

    override fun error(throwable: Throwable) {
        Log.e(getLoggerName(), throwable.message, throwable)
    }
}
