package org.skynetsoftware.avnlauncher.domain.model

data class Game(
    val title: String,
    val imageUrl: String,
    val f95ZoneThreadId: Int,
    val executablePaths: Set<String>,
    val version: String,
    val playTime: Long,
    val rating: Int,
    val f95Rating: Float,
    val updateAvailable: Boolean,
    val added: Long,
    val lastPlayed: Long,
    val hidden: Boolean,
    val releaseDate: Long,
    val firstReleaseDate: Long,
    val playState: PlayState,
    val availableVersion: String?,
    val tags: Set<String>,
    val checkForUpdates: Boolean,
    val firstPlayed: Long,
    val notes: String?,
    val favorite: Boolean,
)
