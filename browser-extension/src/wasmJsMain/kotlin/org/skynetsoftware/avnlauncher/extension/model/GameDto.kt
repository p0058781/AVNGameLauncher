package org.skynetsoftware.avnlauncher.extension.model

import kotlinx.serialization.Serializable

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
    val favorite: Boolean,
    val totalPlayTime: Long,
    val firstPlayedTime: Long,
    val lastPlayedTime: Long,
)
