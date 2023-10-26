package org.skynetsoftware.avnlauncher.logging

internal expect class UncaughtExceptionHandlerLogger {
    fun init(logger: Logger)
}