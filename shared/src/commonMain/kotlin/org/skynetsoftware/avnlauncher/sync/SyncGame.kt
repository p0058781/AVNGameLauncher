package org.skynetsoftware.avnlauncher.sync

import kotlinx.serialization.Serializable
import org.skynetsoftware.avnlauncher.data.model.PlayState

@Serializable
data class SyncGame(
    val title: String,
    val imageUrl: String,
    val f95ZoneThreadId: Int,
    val executablePath: String?,
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
)
