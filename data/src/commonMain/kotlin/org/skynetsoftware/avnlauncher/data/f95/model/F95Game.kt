package org.skynetsoftware.avnlauncher.data.f95.model

internal data class F95Game(
    val threadId: Int,
    val title: String,
    val description: String,
    val developer: String,
    val imageUrl: String,
    val version: String,
    val rating: Float,
    val firstReleaseDate: Long,
    val releaseDate: Long,
    val tags: Set<String>,
    val prefixes: Set<String>,
)
