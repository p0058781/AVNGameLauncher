package org.skynetsoftware.avnlauncher.data.mapper

import io.realm.kotlin.ext.toRealmSet
import org.skynetsoftware.avnlauncher.data.database.model.RealmGame
import org.skynetsoftware.avnlauncher.data.f95.model.F95Game
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlayState

internal fun Game.toRealmGame() =
    RealmGame().apply {
        title = this@toRealmGame.title
        imageUrl = this@toRealmGame.imageUrl
        f95ZoneThreadId = this@toRealmGame.f95ZoneThreadId
        executablePaths = this@toRealmGame.executablePaths.toRealmSet()
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
        tags = this@toRealmGame.tags.toRealmSet()
        lastRedirectUrl = this@toRealmGame.lastRedirectUrl
        checkForUpdates = this@toRealmGame.checkForUpdates
    }

internal fun RealmGame.toGame() =
    Game(
        title = title,
        imageUrl = imageUrl,
        f95ZoneThreadId = f95ZoneThreadId,
        executablePaths = executablePaths,
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
        tags = tags.toSet(),
        lastRedirectUrl = lastRedirectUrl,
        checkForUpdates = checkForUpdates,
    )

internal fun F95Game.toRealmGame() =
    RealmGame().apply {
        title = this@toRealmGame.title
        imageUrl = this@toRealmGame.imageUrl
        f95ZoneThreadId = this@toRealmGame.threadId
        version = this@toRealmGame.version
        f95Rating = this@toRealmGame.rating
        releaseDate = this@toRealmGame.releaseDate
        firstReleaseDate = this@toRealmGame.firstReleaseDate
        tags = this@toRealmGame.tags.toRealmSet()
    }

internal fun F95Game.toGame() =
    Game(
        title = title,
        imageUrl = imageUrl,
        f95ZoneThreadId = threadId,
        executablePaths = emptySet(),
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
        lastRedirectUrl = null,
        checkForUpdates = true,
    )
