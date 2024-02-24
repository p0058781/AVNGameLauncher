package org.skynetsoftware.avnlauncher.state

import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlayState
import kotlin.test.Test
import kotlin.test.assertEquals

class StateHandlerTest {
    private val game = Game(
        "", "", "", 0, emptySet(), "", 0L, 0,
        0f, false, 0L, 0L, 0L, false, 0L, 0L,
        PlayState.Playing, null, emptySet(), null, false, 0, null,
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
