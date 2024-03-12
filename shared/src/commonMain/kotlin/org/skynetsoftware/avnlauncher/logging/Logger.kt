package org.skynetsoftware.avnlauncher.logging

import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.database.model.RealmLog

val loggerKoinModule = module {
    single<Logger> { LoggerImpl(get()) }
}

interface Logger {
    fun info(message: String)

    fun warning(message: String)

    fun error(message: String)

    fun error(throwable: Throwable)
}

private class LoggerImpl(database: Database) : Logger {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val dateTimeFormatter = DateTimeFormatter.ISO_INSTANT

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
        println("$severity[${dateTimeFormatter.format(time)}]: $message")
        coroutineScope.launch {
            transaction {
                RealmLog.insert {
                    it[RealmLog.time] = time.toEpochMilliseconds()
                    it[RealmLog.logMessage] = message
                    it[RealmLog.severity] = severity.name
                }
            }
        }
    }

}