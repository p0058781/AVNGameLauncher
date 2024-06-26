package org.skynetsoftware.avnlauncher.domain.model

private const val ONE_HOUR_MILLIS = 3600_000L

sealed class Filter(val label: String) {
    @Suppress("MemberNameEqualsClassName")
    abstract fun filter(input: List<Game>): List<Game>

    object All : Filter("All") {
        override fun filter(input: List<Game>) = input.filter { !it.hidden }
    }

    object GamesWithUpdate : Filter("Games With Update") {
        override fun filter(input: List<Game>) = All.filter(input).filter { it.updateAvailable }
    }

    object HiddenGames : Filter("Archived Games") {
        override fun filter(input: List<Game>) = input.filter { it.hidden }
    }

    object UnplayedGames : Filter("Unplayed Games") {
        override fun filter(input: List<Game>) = All.filter(input).filter { it.totalPlayTime < ONE_HOUR_MILLIS }
    }

    object Playing : Filter("Playing") {
        override fun filter(input: List<Game>) = All.filter(input).filter { it.playState == PlayState.Playing }
    }

    object Completed : Filter("Completed") {
        override fun filter(input: List<Game>) = All.filter(input).filter { it.playState == PlayState.Completed }
    }

    object WaitingForUpdate : Filter("Waiting For Update") {
        override fun filter(input: List<Game>) = All.filter(input).filter { it.playState == PlayState.WaitingForUpdate }
    }

    object Favorites : Filter("Favorites") {
        override fun filter(input: List<Game>) = All.filter(input).filter { it.favorite }
    }

    companion object {
        val entries: List<Filter> by lazy {
            listOf(Playing, GamesWithUpdate, WaitingForUpdate, Favorites, Completed, All, HiddenGames, UnplayedGames)
        }
    }
}
