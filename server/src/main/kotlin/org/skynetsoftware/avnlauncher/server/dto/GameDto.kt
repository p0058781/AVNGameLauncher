package org.skynetsoftware.avnlauncher.server.dto

import kotlinx.serialization.Serializable
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlayState

@Serializable
data class GameDto(
    val f95ZoneThreadId: Int,
    val rating: Int,
    val updateAvailable: Boolean,
    val added: Long,
    val hidden: Boolean,
    val playState: PlayState,
    val availableVersion: String?,
    val notes: String?,
    val favorite: Boolean,
    val totalPlayTime: Long,
    val firstPlayedTime: Long,
    val lastPlayedTime: Long,
)

fun Game.toGameDto() =
    GameDto(
        f95ZoneThreadId = f95ZoneThreadId,
        rating = rating,
        updateAvailable = updateAvailable,
        added = added,
        hidden = hidden,
        playState = playState,
        availableVersion = availableVersion,
        notes = notes,
        favorite = favorite,
        totalPlayTime = totalPlayTime,
        firstPlayedTime = firstPlayedTime,
        lastPlayedTime = lastPlayedTime,
    )
