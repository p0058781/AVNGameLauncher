package org.skynetsoftware.avnlauncher.f95.model

data class F95Game(
    val threadId: Int,
    val title: String,
    val imageUrl: String,
    val version: String,
    val rating: Float,
    val firstReleaseDate: Long,
    val releaseDate: Long,
    val tags: Set<String>,
)
