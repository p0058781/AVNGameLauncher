package org.skynetsoftware.avnlauncher.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TimeUtilsKtTest {
    @Test
    fun `averagePlayTime correct result`() {
        val firstPlayedTime = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000
        val totalPlayTime = 4 * 60 * 60 * 1000L
        val result = calculateAveragePlayTime(firstPlayedTime, totalPlayTime)
        assertEquals(2.0f, result)
    }

    @Test
    fun `averagePlayTime firstPlayedTime less then 24h`() {
        val firstPlayed = System.currentTimeMillis() - 1
        val totalPlayTime = 1L
        val result = calculateAveragePlayTime(firstPlayed, totalPlayTime)
        assertTrue(result < 1)
    }

    @Test
    fun `averagePlayTime zero`() {
        val firstPlayedTime = 0L
        val totalPlayTime = 0L
        val result = calculateAveragePlayTime(firstPlayedTime, totalPlayTime)
        assertEquals(0f, result)
    }

    @Test
    fun `averagePlayTime larger than 24h`() {
        val firstPlayedTime = System.currentTimeMillis() - 23 * 60 * 60 * 1000
        val totalPlayTime = 2 * 24 * 60 * 60 * 1000L
        val result = calculateAveragePlayTime(firstPlayedTime, totalPlayTime)
        assertEquals(result, 24f)
    }
}
