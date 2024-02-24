package org.skynetsoftware.avnlauncher.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlayStateTest {
    @Test
    fun `fromString returns correct value`() {
        val values = PlayState.entries.toTypedArray()
        values.forEach {
            assertEquals(it, PlayState.fromString(it.toString()))
        }
    }

    @Test
    fun `fromString throws exception for null value`() {
        try {
            PlayState.fromString(null)
            assertFalse(false)
        } catch (e: IllegalStateException) {
            assertTrue(true)
        }
    }

    @Test
    fun `fromString throws exception for invalid value`() {
        try {
            PlayState.fromString("invalid value")
            assertFalse(false)
        } catch (e: IllegalStateException) {
            assertTrue(true)
        }
    }
}
