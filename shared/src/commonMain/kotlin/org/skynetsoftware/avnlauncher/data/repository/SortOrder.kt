package org.skynetsoftware.avnlauncher.data.repository

import org.skynetsoftware.avnlauncher.data.model.Game

sealed class SortOrder(val label: String) {
    abstract fun sort(input: List<Game>, sortDirection: SortDirection): List<Game>

    object AZ : SortOrder("A-Z") {
        override fun sort(input: List<Game>, sortDirection: SortDirection): List<Game> {
            return when (sortDirection) {
                SortDirection.Ascending -> input.sortedBy { it.title }
                SortDirection.Descending -> input.sortedByDescending { it.title }
            }
        }
    }

    object LastPlayed : SortOrder("Last Played") {
        override fun sort(input: List<Game>, sortDirection: SortDirection): List<Game> {
            return when (sortDirection) {
                SortDirection.Ascending -> input.sortedBy { it.lastPlayed }
                SortDirection.Descending -> input.sortedByDescending { it.lastPlayed }
            }
        }
    }

    object Added : SortOrder("Added") {
        override fun sort(input: List<Game>, sortDirection: SortDirection): List<Game> {
            return when (sortDirection) {
                SortDirection.Ascending -> input.sortedBy { it.added }
                SortDirection.Descending -> input.sortedByDescending { it.added }
            }
        }
    }

    object Rating : SortOrder("Rating") {
        override fun sort(input: List<Game>, sortDirection: SortDirection): List<Game> {
            return when (sortDirection) {
                SortDirection.Ascending -> input.sortedBy { it.rating }
                SortDirection.Descending -> input.sortedByDescending { it.rating }
            }
        }
    }

    object PlayTime : SortOrder("Play Time") {
        override fun sort(input: List<Game>, sortDirection: SortDirection): List<Game> {
            return when (sortDirection) {
                SortDirection.Ascending -> input.sortedBy { it.playTime }
                SortDirection.Descending -> input.sortedByDescending { it.playTime }
            }
        }
    }

    object UpdateAvailable : SortOrder("Update Available") {
        override fun sort(input: List<Game>, sortDirection: SortDirection): List<Game> {
            return when (sortDirection) {
                SortDirection.Ascending -> input.sortedBy { it.updateAvailable }
                SortDirection.Descending -> input.sortedByDescending { it.updateAvailable }
            }
        }
    }

    object ReleaseDate : SortOrder("Release Date") {
        override fun sort(input: List<Game>, sortDirection: SortDirection): List<Game> {
            return when (sortDirection) {
                SortDirection.Ascending -> input.sortedBy { it.releaseDate }
                SortDirection.Descending -> input.sortedByDescending { it.releaseDate }
            }
        }
    }

    companion object {
        val entries: List<SortOrder> by lazy {
            listOf(
                AZ,
                LastPlayed,
                Added,
                Rating,
                PlayTime,
                UpdateAvailable,
                ReleaseDate
            )
        }
    }
}

enum class SortDirection(val label: String) {
    Ascending("↑"), Descending("↓")
}