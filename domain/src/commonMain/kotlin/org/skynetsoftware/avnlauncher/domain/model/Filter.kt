package org.skynetsoftware.avnlauncher.domain.model

private const val ONE_HOUR_MILLIS = 3600_000L

sealed class Filter {
    val name = this::class.simpleName!!
    open val data: String? = null

    @Suppress("MemberNameEqualsClassName")
    abstract fun filter(input: List<Game>): List<Game>

    object All : Filter() {
        override fun filter(input: List<Game>) = input.filter { !it.hidden }
    }

    object GamesWithUpdate : Filter() {
        override fun filter(input: List<Game>) = All.filter(input).filter { it.updateAvailable }
    }

    object HiddenGames : Filter() {
        override fun filter(input: List<Game>) = input.filter { it.hidden }
    }

    object UnplayedGames : Filter() {
        override fun filter(input: List<Game>) = All.filter(input).filter { it.totalPlayTime < ONE_HOUR_MILLIS }
    }

    class PlayState(private val playStateId: String) : Filter() {
        override val data = playStateId

        override fun filter(input: List<Game>) = All.filter(input).filter { it.playState.id == playStateId }
    }

    class Lists(private val listId: Int) : Filter() {
        override val data = listId.toString()

        override fun filter(input: List<Game>) = All.filter(input).filter { it.lists.find { it.id == listId } != null }
    }

    companion object {
        fun fromNameAndData(
            name: String,
            data: String?,
        ): Filter {
            val fallback = All
            return when (name) {
                All::class.simpleName -> All
                GamesWithUpdate::class.simpleName -> GamesWithUpdate
                HiddenGames::class.simpleName -> HiddenGames
                UnplayedGames::class.simpleName -> UnplayedGames
                PlayState::class.simpleName -> {
                    if (data == null) {
                        fallback
                    } else {
                        PlayState(data)
                    }
                }
                Lists::class.simpleName -> {
                    val dataInt = data?.toIntOrNull()
                    if (dataInt == null) {
                        fallback
                    } else {
                        Lists(dataInt)
                    }
                }
                else -> fallback
            }
        }
    }
}
