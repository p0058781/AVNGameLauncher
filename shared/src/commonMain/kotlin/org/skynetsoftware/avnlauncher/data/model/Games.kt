package org.skynetsoftware.avnlauncher.data.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Games : IntIdTable(
    name = "game"
) {
    val title = text("title")
    val imageUrl = text("imageUrl")
    val f95ZoneUrl = text("f95ZoneUrl")
    val executablePath = text("executablePath")
    val version = text("version")
    val playTime = long("playTime").nullable()
    val rating = integer("rating").nullable()
    val updateAvailable = bool("updateAvailable")
    val added = long("added")
    val lastPlayed = long("lastPlayed")
    val lastUpdateCheck = long("lastUpdateCheck")
    val hidden = bool("hidden")
    val releaseDate = text("releaseDate").nullable()
    val completed = bool("completed")
    val playing = bool("playing")
    val waitingForUpdate = bool("waitingForUpdate")
    val availableVersion = text("availableVersion").nullable()
}

class Game(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Game>(Games)

    val title by Games.title
    val imageUrl by Games.imageUrl
    val f95ZoneUrl by Games.f95ZoneUrl
    val executablePath by Games.executablePath
    val version by Games.version
    val playTime by Games.playTime
    val rating by Games.rating
    val updateAvailable by Games.updateAvailable
    val added by Games.added
    val lastPlayed by Games.lastPlayed
    val lastUpdateCheck by Games.lastUpdateCheck
    val hidden by Games.hidden
    val releaseDate by Games.releaseDate
    val completed by Games.completed
    val playing by Games.playing
    val waitingForUpdate by Games.waitingForUpdate
    val availableVersion by Games.availableVersion

}