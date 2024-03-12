package org.skynetsoftware.avnlauncher.data.model

import io.realm.kotlin.ext.realmSetOf
import org.skynetsoftware.avnlauncher.data.database.model.RealmGame
import org.skynetsoftware.avnlauncher.data.model.legacy.V1Game
import org.skynetsoftware.avnlauncher.f95.model.F95Game

data class Game(
    val title: String,
    val imageUrl: String,
    val f95ZoneThreadId: Int,
    val executablePath: String?,//TODO allow multiple versions
    val version: String,
    val playTime: Long,
    val rating: Int,
    val f95Rating: Float,
    val updateAvailable: Boolean,
    val added: Long,
    val lastPlayed: Long,
    val lastUpdateCheck: Long,
    val hidden: Boolean,
    val releaseDate: Long,
    val firstReleaseDate: Long,
    val playState: PlayState,
    val availableVersion: String?,
    val tags: Set<String>,
)

fun Game.toRealmGame() = RealmGame().apply {
    title = this@toRealmGame.title
    imageUrl = this@toRealmGame.imageUrl
    f95ZoneThreadId = this@toRealmGame.f95ZoneThreadId
    executablePath = this@toRealmGame.executablePath
    version = this@toRealmGame.version
    playTime = this@toRealmGame.playTime
    rating = this@toRealmGame.rating
    f95Rating = this@toRealmGame.f95Rating
    updateAvailable = this@toRealmGame.updateAvailable
    added = this@toRealmGame.added
    lastPlayed = this@toRealmGame.lastPlayed
    lastUpdateCheck = this@toRealmGame.lastUpdateCheck
    hidden = this@toRealmGame.hidden
    releaseDate = this@toRealmGame.releaseDate
    firstReleaseDate = this@toRealmGame.firstReleaseDate
    playState = this@toRealmGame.playState.name
    availableVersion = this@toRealmGame.availableVersion
    tags = realmSetOf(*this@toRealmGame.tags.toTypedArray())
}

fun RealmGame.toGame() = Game(
    title = title,
    imageUrl = imageUrl,
    f95ZoneThreadId = f95ZoneThreadId,
    executablePath = executablePath,
    version = version,
    playTime = playTime,
    rating = rating,
    f95Rating = f95Rating,
    updateAvailable = updateAvailable,
    added = added,
    lastPlayed = lastPlayed,
    lastUpdateCheck = lastUpdateCheck,
    hidden = hidden,
    releaseDate = releaseDate,
    firstReleaseDate = firstReleaseDate,
    playState = PlayState.fromString(playState),
    availableVersion = availableVersion,
    tags = setOf(*tags.toTypedArray())
)

fun F95Game.toRealmGame() = RealmGame().apply {
    title = this@toRealmGame.title
    imageUrl = this@toRealmGame.imageUrl
    f95ZoneThreadId = this@toRealmGame.threadId
    version = this@toRealmGame.version
    f95Rating = this@toRealmGame.rating
    releaseDate = this@toRealmGame.releaseDate
    firstReleaseDate = this@toRealmGame.firstReleaseDate
    tags = realmSetOf(*this@toRealmGame.tags.toTypedArray())
}

fun F95Game.toGame() = Game(
    title = title,
    imageUrl = imageUrl,
    f95ZoneThreadId = threadId,
    executablePath = null,
    version = version,
    playTime = 0L,
    rating = 0,
    f95Rating = rating,
    updateAvailable = false,
    added = 0L,
    lastPlayed = 0L,
    lastUpdateCheck = 0L,
    hidden = false,
    releaseDate = releaseDate,
    firstReleaseDate = firstReleaseDate,
    playState = PlayState.None,
    availableVersion = null,
    tags = tags
)

fun convertF95GameAndV1GameToGame(f95Game: F95Game, v1Game: V1Game) = Game(
    title = f95Game.title,
    imageUrl = v1Game.imageUrl,
    f95ZoneThreadId = f95Game.threadId,
    executablePath = v1Game.executablePath,
    version = v1Game.version,
    playTime = v1Game.playTime ?: 0L,
    rating = v1Game.rating ?: 0,
    f95Rating = f95Game.rating,
    updateAvailable = v1Game.updateAvailable == 1,
    added = v1Game.added,
    lastPlayed = v1Game.lastPlayed,
    lastUpdateCheck = 0L,
    hidden = v1Game.hidden == 1,
    releaseDate = f95Game.releaseDate,
    firstReleaseDate = f95Game.releaseDate,
    playState = if (v1Game.playing == 1) PlayState.Playing else if (v1Game.completed == 1) PlayState.Completed else if (v1Game.waitingForUpdate == 1) PlayState.WaitingForUpdate else PlayState.None,
    availableVersion = v1Game.availableVersion,
    tags = f95Game.tags
)

fun V1Game.toGame() = Game(
    title = title,
    imageUrl = imageUrl,
    f95ZoneThreadId = 0,
    executablePath = executablePath,
    version = version,
    playTime = playTime ?: 0L,
    rating = rating ?: 0,
    f95Rating = 0f,
    updateAvailable = updateAvailable == 1,
    added = added,
    lastPlayed = lastPlayed,
    lastUpdateCheck = 0L,
    hidden = hidden == 1,
    releaseDate = 0L,
    firstReleaseDate = 0L,
    playState = if (playing == 1) PlayState.Playing else if (completed == 1) PlayState.Completed else if (waitingForUpdate == 1) PlayState.WaitingForUpdate else PlayState.None,
    availableVersion = null,
    tags = emptySet()
)