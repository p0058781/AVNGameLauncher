package org.skynetsoftware.avnlauncher.domain.model.importexport

import kotlinx.serialization.Serializable
import org.skynetsoftware.avnlauncher.domain.model.PlayState

/**
 * Data Class that has same properties as [Game] but all properties are nullable,
 * and required properties are checked in runtime when importing.
 * The reason for this is to support older exports
 */
@Serializable
data class SerializedGame(
    val title: String?,
    val imageUrl: String?,
    val f95ZoneThreadId: Int?,
    val executablePaths: Set<String>?,
    val version: String?,
    val playTime: Long?,
    val rating: Int?,
    val f95Rating: Float?,
    val updateAvailable: Boolean?,
    val added: Long?,
    val lastPlayed: Long?,
    val hidden: Boolean?,
    val releaseDate: Long?,
    val firstReleaseDate: Long?,
    val playState: PlayState?,
    val availableVersion: String?,
    val tags: Set<String>?,
    val checkForUpdates: Boolean?,
    val firstPlayed: Long?,
    val notes: String?,
    val favorite: Boolean?,
)