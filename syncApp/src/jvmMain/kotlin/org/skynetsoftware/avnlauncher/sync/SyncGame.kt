package org.skynetsoftware.avnlauncher.sync

import kotlinx.serialization.Serializable
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlayState

@Serializable
data class SyncGame(
    val title: String,
    val imageUrl: String,
    val customImageUrl: String?,
    val f95ZoneThreadId: Int,
    val executablePaths: Set<String>,
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
    val lastRedirectUrl: String?,
    val checkForUpdates: Boolean,
    val firstPlayed: Long,
    val notes: String?,
)

fun SyncGame.toGame() =
    Game(
        title = title,
        imageUrl = imageUrl,
        customImageUrl = customImageUrl,
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
        playState = playState,
        availableVersion = availableVersion,
        tags = tags,
        lastRedirectUrl = lastRedirectUrl,
        checkForUpdates = checkForUpdates,
        firstPlayed = firstPlayed,
        notes = notes,
    )

fun Game.toSyncGame() =
    SyncGame(
        title = title,
        imageUrl = imageUrl,
        customImageUrl = customImageUrl,
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
        playState = playState,
        availableVersion = availableVersion,
        tags = tags,
        lastRedirectUrl = lastRedirectUrl,
        checkForUpdates = checkForUpdates,
        firstPlayed = firstPlayed,
        notes = notes,
    )
