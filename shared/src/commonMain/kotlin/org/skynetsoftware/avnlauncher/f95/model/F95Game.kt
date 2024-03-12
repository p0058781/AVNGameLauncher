package org.skynetsoftware.avnlauncher.f95.model

data class F95Game(
    val threadId: Int,
    val title: String,
    val imageUrl: String,
    val version: String,
    val rating: Int,
    val firstReleaseDate: String,
    val releaseDate: String,
    val tags: Set<String>,
)