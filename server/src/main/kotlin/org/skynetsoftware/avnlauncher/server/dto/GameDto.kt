package org.skynetsoftware.avnlauncher.server.dto

import kotlinx.serialization.Serializable
import org.skynetsoftware.avnlauncher.domain.model.Game

@Serializable
data class GameDto(
    val f95ZoneThreadId: Int,
    val rating: Int,
    val updateAvailable: Boolean,
    val added: Long,
    val hidden: Boolean,
    val playState: String,
    val availableVersion: String?,
    val notes: String?,
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
        playState = playState.id,
        availableVersion = availableVersion,
        notes = notes,
        totalPlayTime = totalPlayTime,
        firstPlayedTime = firstPlayedTime,
        lastPlayedTime = lastPlayedTime,
    )
