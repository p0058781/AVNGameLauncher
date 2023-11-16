package org.skynetsoftware.avnlauncher.data.model.legacy

import kotlinx.serialization.Serializable

@Serializable
class V1Game(
    val title: String,
    val added: Long,
    val lastPlayed: Long,
    val playTime: Long?,
    val completed: Int,
    val hidden: Int,
    val playing: Int,
    val waitingForUpdate: Int,
    val updateAvailable: Int,
    val executablePath: String?,
    val f95ZoneUrl: String,
    val imageUrl: String,
    val rating: Int?,
    val availableVersion: String?,
    val releaseDate: String?,
    val version: String,
)