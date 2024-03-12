package org.skynetsoftware.avnlauncher.data.model

import org.jetbrains.exposed.sql.Column
import org.skynetsoftware.avnlauncher.data.model.Games

enum class SortOrder(val label: String, val column: Column<out Any?>) {
    AZ("A-Z", Games.title),
    LastPlayed("Last Played", Games.lastPlayed),
    Added("Added", Games.added),
    Rating("Rating", Games.rating),
    PlayTime("Play Time", Games.playTime),
    UpdateAvailable("Update Available", Games.updateAvailable),
    ReleaseDate("Release Date", Games.releaseDate),
}

enum class SortDirection(val label: String, val sortOrder: org.jetbrains.exposed.sql.SortOrder) {
    Ascending("↑", org.jetbrains.exposed.sql.SortOrder.ASC), Descending("↓", org.jetbrains.exposed.sql.SortOrder.DESC)
}