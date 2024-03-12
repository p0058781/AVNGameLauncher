package org.skynetsoftware.avnlauncher.logging

import io.realm.kotlin.Realm
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.database.model.RealmLog
import org.skynetsoftware.avnlauncher.utils.SimpleDateFormat

expect fun logUncaughtExceptions(logger: Logger)

val loggerKoinModule = module {
    single<Logger> { LoggerImpl(get()) }
}

interface Logger {
    fun info(message: String)

    fun warning(message: String)

    fun error(message: String)

    fun error(throwable: Throwable)
}

private class LoggerImpl(private val realm: Realm) : Logger {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
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

    private fun log(severity: Severity, message: String) {
        val time = Clock.System.now()
        println("$severity[${timeFormat.format(time.toEpochMilliseconds())}]: $message")
        coroutineScope.launch {
            realm.write {
                copyToRealm(RealmLog().apply {
                    this.severity = severity.name
                    this.logMessage = message
                })
            }
        }
    }

}