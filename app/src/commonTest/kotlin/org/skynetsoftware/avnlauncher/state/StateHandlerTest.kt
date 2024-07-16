package org.skynetsoftware.avnlauncher.state

import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlayState
import kotlin.test.Test
import kotlin.test.assertEquals

class StateHandlerTest {
    private val game = Game(
        title = "",
        description = "",
        developer = "",
        imageUrl = "",
        f95ZoneThreadId = 0,
        executablePaths = emptySet(),
        version = "",
        rating = 0,
        f95Rating = 0f,
        updateAvailable = false,
        added = 0L,
        hidden = false,
        releaseDate = 0L,
        firstReleaseDate = 0L,
        playState = PlayState(
            id = "All",
            label = "All",
            description = null,
        ),
        availableVersion = null,
        tags = emptySet(),
        checkForUpdates = false,
        notes = null,
        playSessions = emptyList(),
        lists = emptyList(),
        totalPlayTime = 0L,
        firstPlayedTime = 0L,
        lastPlayedTime = 0,
    )

    @Test
    fun `verify that State subclasses have unique ids`() {
        val subclasses = State::class.sealedSubclasses
        val ids = mutableSetOf<Int>()

        subclasses.forEach {
            when (it) {
                State.Idle::class -> {
                    ids.add(State.Idle.id)
                }
                State.UpdateCheckRunning::class -> {
                    ids.add(State.UpdateCheckRunning.id)
                }
                State.Playing::class -> {
                    ids.add(State.Playing(game).id)
                }
            }
        }

        assertEquals(ids.size, subclasses.size)
    }
}
