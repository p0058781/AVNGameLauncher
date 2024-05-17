package org.skynetsoftware.avnlauncher.data.mapper

import kotlinx.datetime.Clock
import org.skynetsoftware.avnlauncher.data.GameEntity
import org.skynetsoftware.avnlauncher.data.GameWithPlaySessions
import org.skynetsoftware.avnlauncher.data.GamesWithPlaySessions
import org.skynetsoftware.avnlauncher.data.f95.model.F95Game
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlaySession
import org.skynetsoftware.avnlauncher.domain.model.PlayState

internal fun Game.toGameEntity() =
    GameEntity(
        title = title,
        description = description,
        developer = developer,
        imageUrl = imageUrl,
        customImageUrl = null,
        f95ZoneThreadId = f95ZoneThreadId,
        executablePaths = executablePaths,
        version = version,
        playTime = 0L,
        rating = rating,
        f95Rating = f95Rating,
        updateAvailable = updateAvailable,
        added = added,
        lastPlayed = 0L,
        hidden = hidden,
        releaseDate = releaseDate,
        firstReleaseDate = firstReleaseDate,
        playState = playState,
        availableVersion = availableVersion,
        tags = tags,
        checkForUpdates = checkForUpdates,
        firstPlayed = 0L,
        notes = notes,
        favorite = favorite,
    )

internal fun GameEntity.toGame() =
    Game(
        title = this.title,
        description = this.description,
        developer = this.developer,
        imageUrl = this.imageUrl,
        f95ZoneThreadId = f95ZoneThreadId,
        executablePaths = this.executablePaths,
        version = this.version,
        totalPlayTime = this.playTime,
        rating = this.rating,
        f95Rating = this.f95Rating,
        updateAvailable = this.updateAvailable,
        added = this.added,
        lastPlayedTime = this.lastPlayed,
        hidden = this.hidden,
        releaseDate = this.releaseDate,
        firstReleaseDate = this.firstReleaseDate,
        playState = this.playState,
        availableVersion = this.availableVersion,
        tags = this.tags.toSet(),
        checkForUpdates = this.checkForUpdates,
        firstPlayedTime = this.firstPlayed,
        notes = this.notes,
        favorite = this.favorite,
        playSessions = emptyList(),
    )

internal fun List<GamesWithPlaySessions>.toGames(): List<Game> {
    return groupBy { it.f95ZoneThreadId }
        .map { (f95ZoneThreadId, games) ->
            val first = games.first()
            val playSessions = games.mapNotNull {
                if (it.playSessionStartTime == null || it.playSessionEndTime == null) {
                    null
                } else {
                    PlaySession(
                        gameId = first.f95ZoneThreadId,
                        startTime = it.playSessionStartTime,
                        endTime = it.playSessionEndTime,
                        version = it.version,
                    )
                }
            }
            val playSessionsFirstPlayedTime = playSessions.minOfOrNull { it.startTime } ?: 0L
            val firstPlayedTime = if (first.firstPlayed > 0) {
                first.firstPlayed
            } else {
                playSessionsFirstPlayedTime
            }
            Game(
                title = first.title,
                description = first.description,
                developer = first.developer,
                imageUrl = first.imageUrl,
                f95ZoneThreadId = f95ZoneThreadId,
                executablePaths = first.executablePaths,
                version = first.version,
                totalPlayTime = first.playTime + playSessions.sumOf { it.endTime - it.startTime },
                rating = first.rating,
                f95Rating = first.f95Rating,
                updateAvailable = first.updateAvailable,
                added = first.added,
                lastPlayedTime = maxOf(first.lastPlayed, playSessions.maxOfOrNull { it.endTime } ?: 0L),
                hidden = first.hidden,
                releaseDate = first.releaseDate,
                firstReleaseDate = first.firstReleaseDate,
                playState = first.playState,
                availableVersion = first.availableVersion,
                tags = first.tags.toSet(),
                checkForUpdates = first.checkForUpdates,
                firstPlayedTime = firstPlayedTime,
                notes = first.notes,
                favorite = first.favorite,
                playSessions = playSessions,
            )
        }
}

internal fun List<GameWithPlaySessions>.toGame(): Game? {
    val first = firstOrNull() ?: return null
    val playSessions = mapNotNull {
        if (it.playSessionStartTime == null || it.playSessionEndTime == null) {
            null
        } else {
            PlaySession(
                gameId = first.f95ZoneThreadId,
                startTime = it.playSessionStartTime,
                endTime = it.playSessionEndTime,
                version = it.version,
            )
        }
    }
    val playSessionsFirstPlayedTime = playSessions.minOfOrNull { it.startTime } ?: 0L
    val firstPlayedTime = if (first.firstPlayed > 0) {
        first.firstPlayed
    } else {
        playSessionsFirstPlayedTime
    }
    return Game(
        title = first.title,
        description = first.description,
        developer = first.developer,
        imageUrl = first.imageUrl,
        f95ZoneThreadId = first.f95ZoneThreadId,
        executablePaths = first.executablePaths,
        version = first.version,
        totalPlayTime = first.playTime + playSessions.sumOf { it.endTime - it.startTime },
        rating = first.rating,
        f95Rating = first.f95Rating,
        updateAvailable = first.updateAvailable,
        added = first.added,
        lastPlayedTime = maxOf(first.lastPlayed, playSessions.maxOfOrNull { it.endTime } ?: 0L),
        hidden = first.hidden,
        releaseDate = first.releaseDate,
        firstReleaseDate = first.firstReleaseDate,
        playState = first.playState,
        availableVersion = first.availableVersion,
        tags = first.tags.toSet(),
        checkForUpdates = first.checkForUpdates,
        firstPlayedTime = firstPlayedTime,
        notes = first.notes,
        favorite = first.favorite,
        playSessions = playSessions,
    )
}

internal fun F95Game.toGame() =
    Game(
        title = title,
        description = description,
        developer = developer,
        imageUrl = imageUrl,
        f95ZoneThreadId = threadId,
        executablePaths = emptySet(),
        version = version,
        totalPlayTime = 0L,
        rating = 0,
        f95Rating = rating,
        updateAvailable = false,
        added = Clock.System.now().toEpochMilliseconds(),
        lastPlayedTime = 0L,
        hidden = false,
        releaseDate = releaseDate,
        firstReleaseDate = firstReleaseDate,
        playState = PlayState.Playing,
        availableVersion = null,
        tags = tags,
        checkForUpdates = true,
        firstPlayedTime = 0L,
        notes = null,
        favorite = false,
        playSessions = emptyList(),
    )
