package org.skynetsoftware.avnlauncher.data.mapper

import kotlinx.datetime.Clock
import org.skynetsoftware.avnlauncher.data.GameEntity
import org.skynetsoftware.avnlauncher.data.f95.model.F95Game
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlayState

internal fun Game.toGameEntity() =
    GameEntity(
        title = title,
        imageUrl = imageUrl,
        customImageUrl = null,
        f95ZoneThreadId = f95ZoneThreadId,
        executablePaths = executablePaths,
        version = version,
        playTime = playTime,
        rating = rating,
        f95Rating = f95Rating,
        updateAvailable = updateAvailable,
        added = added,
        lastPlayed = lastPlayed,
        hidden = hidden,
        releaseDate = releaseDate,
        firstReleaseDate = firstReleaseDate,
        playState = playState,
        availableVersion = availableVersion,
        tags = tags,
        checkForUpdates = checkForUpdates,
        firstPlayed = firstPlayed,
        notes = notes,
        favorite = favorite,
    )

internal fun GameEntity.toGame() =
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
        hidden = hidden,
        releaseDate = releaseDate,
        firstReleaseDate = firstReleaseDate,
        playState = playState,
        availableVersion = availableVersion,
        tags = tags.toSet(),
        checkForUpdates = checkForUpdates,
        firstPlayed = firstPlayed,
        notes = notes,
        favorite = favorite,
    )

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
        added = Clock.System.now().toEpochMilliseconds(),
        lastPlayed = 0L,
        hidden = false,
        releaseDate = releaseDate,
        firstReleaseDate = firstReleaseDate,
        playState = PlayState.Playing,
        availableVersion = null,
        tags = tags,
        checkForUpdates = true,
        firstPlayed = Clock.System.now().toEpochMilliseconds(),
        notes = null,
        favorite = false,
    )
