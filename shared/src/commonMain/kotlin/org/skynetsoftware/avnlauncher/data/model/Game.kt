package org.skynetsoftware.avnlauncher.data.model

import org.skynetsoftware.avnlauncher.data.database.model.RealmGame
import org.skynetsoftware.avnlauncher.f95.model.F95Game

data class Game(
    val title: String,
    val imageUrl: String,
    val f95ZoneUrl: String,
    val executablePath: String?,
    val version: String,
    val playTime: Long,
    val rating: Int,
    val updateAvailable: Boolean,
    val added: Long,
    val lastPlayed: Long,
    val lastUpdateCheck: Long,
    val hidden: Boolean,
    val releaseDate: String?,
    val playState: PlayState,
    val availableVersion: String?,
)

fun Game.toRealmGame() = RealmGame().apply {
    title = this@toRealmGame.title
    imageUrl = this@toRealmGame.imageUrl
    f95ZoneUrl = this@toRealmGame.f95ZoneUrl
    executablePath = this@toRealmGame.executablePath
    version = this@toRealmGame.version
    playTime = this@toRealmGame.playTime
    rating = this@toRealmGame.rating
    updateAvailable = this@toRealmGame.updateAvailable
    added = this@toRealmGame.added
    lastPlayed = this@toRealmGame.lastPlayed
    lastUpdateCheck = this@toRealmGame.lastUpdateCheck
    hidden = this@toRealmGame.hidden
    releaseDate = this@toRealmGame.releaseDate
    playState = this@toRealmGame.playState.name
    availableVersion = this@toRealmGame.availableVersion
}

fun RealmGame.toGame() = Game(
    title = title,
    imageUrl = imageUrl,
    f95ZoneUrl = f95ZoneUrl,
    executablePath = executablePath,
    version = version,
    playTime = playTime,
    rating = rating,
    updateAvailable = updateAvailable,
    added = added,
    lastPlayed = lastPlayed,
    lastUpdateCheck = lastUpdateCheck,
    hidden = hidden,
    releaseDate = releaseDate,
    playState = PlayState.fromString(playState),
    availableVersion = availableVersion
)

fun F95Game.toRealmGame() = RealmGame().apply {
    title = this@toRealmGame.title
    imageUrl = this@toRealmGame.imageUrl
    f95ZoneUrl = this@toRealmGame.f95ZoneUrl
    version = this@toRealmGame.version
    rating = this@toRealmGame.rating
    releaseDate = this@toRealmGame.releaseDate
}