package org.skynetsoftware.avnlauncher.domain.model

data class PlaySession(
    val gameId: Int,
    val startTime: Long,
    val endTime: Long,
    val version: String?,
)
