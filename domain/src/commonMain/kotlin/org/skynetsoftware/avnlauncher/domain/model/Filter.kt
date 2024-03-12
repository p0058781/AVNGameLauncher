package org.skynetsoftware.avnlauncher.domain.model

sealed class Filter(val label: String) {
    abstract fun filter(input: List<Game>): List<Game>

    object All : Filter("All") {
        override fun filter(input: List<Game>) = input.filter { !it.hidden }
    }

    object GamesWithUpdate : Filter("Games With Update") {
        override fun filter(input: List<Game>) = All.filter(input).filter { it.updateAvailable }
    }

    object HiddenGames : Filter("Hidden Games") {
        override fun filter(input: List<Game>) = input.filter { it.hidden }
    }

    object UnplayedGames : Filter("Unplayed Games") {
        override fun filter(input: List<Game>) = All.filter(input).filter { it.playTime < 3600_000L }
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

    companion object {
        val entries: List<Filter> by lazy {
            listOf(All, GamesWithUpdate, HiddenGames, UnplayedGames, Playing, Completed, WaitingForUpdate)
        }
    }
}
