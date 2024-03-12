package org.skynetsoftware.avnlauncher.data.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Logs : IntIdTable(
    name = "log"
) {
    val time = long("time")
    val severity = text("severity")
    val logMessage = text("logMessage")
}

class Log(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Log>(Logs)

    val time by Logs.time
    val severity by Logs.severity
    val logMessage by Logs.logMessage
}