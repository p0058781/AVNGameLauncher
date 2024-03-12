package org.skynetsoftware.avnlauncher.data.model

import io.realm.kotlin.ext.realmSetOf
import org.skynetsoftware.avnlauncher.data.database.model.RealmGame
import org.skynetsoftware.avnlauncher.f95.model.F95Game
import org.skynetsoftware.avnlauncher.sync.SyncGame

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
    val lastRedirectUrl: String?
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
    tags = setOf(*tags.toTypedArray()),
    lastRedirectUrl = lastRedirectUrl
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
    playState = PlayState.Playing,
    availableVersion = null,
    tags = tags,
    lastRedirectUrl = null
)

fun SyncGame.toGame() = Game(
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
    playState = playState,
    availableVersion = availableVersion,
    tags = tags,
    lastRedirectUrl = lastRedirectUrl
)

fun Game.toSyncGame() = SyncGame(
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
    playState = playState,
    availableVersion = availableVersion,
    tags = tags,
    lastRedirectUrl = lastRedirectUrl
)