package org.skynetsoftware.avnlauncher.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class PlayStateTest {
    @Test
    fun `fromString returns correct value`() {
        val values = PlayState.entries.toTypedArray()
        values.forEach {
            assertEquals(it, PlayState.fromString(it.toString()))
        }
    }

    @Test
    fun `fromString returns None for null value`() {
        assertEquals(PlayState.None, PlayState.fromString(null))
    }

    @Test
    fun `fromString returns None for invalid value`() {
        assertEquals(PlayState.None, PlayState.fromString("bla bla"))
    }
}
