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
        val stacktraceElement = Thread.currentThread().stackTrace.last()
        return buildString {
            append(stacktraceElement.methodName) // TODO include simple class name
        }
    }
}
