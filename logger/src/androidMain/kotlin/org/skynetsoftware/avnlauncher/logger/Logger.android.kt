package org.skynetsoftware.avnlauncher.logger

import android.util.Log
import org.koin.dsl.module

actual val loggerKoinModule = module {
    single<Logger> { LoggerImpl() }
}

private class LoggerImpl : Logger {
    override fun verbose(message: String) {
        Log.v(getTag(), message)
    }

    override fun debug(message: String) {
        Log.d(getTag(), message)
    }

    override fun info(message: String) {
        Log.i(getTag(), message)
    }

    override fun warning(message: String) {
        Log.w(getTag(), message)
    }

    override fun error(message: String) {
        Log.e(getTag(), message)
    }

    override fun error(throwable: Throwable) {
        Log.e(getTag(), throwable.message, throwable)
    }

    private fun getTag(): String {
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
        return stacktraceElement?.className?.split(".")?.lastOrNull() ?: "Logger"
    }
}
