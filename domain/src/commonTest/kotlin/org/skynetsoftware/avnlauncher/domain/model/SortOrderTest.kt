package org.skynetsoftware.avnlauncher.domain.model

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class SortOrderTest {
    private val titlesSorted = listOf(
        createGame(title = "Banking on Bella"),
        createGame(title = "Guilty Pleasure"),
        createGame(title = "Life Changes for Keely"),
        createGame(title = "New Beginnings in Japan"),
        createGame(title = "One Night Stand"),
        createGame(title = "RSSU"),
        createGame(title = "Reclaiming the Lost"),
        createGame(title = "Shards of the Past"),
        createGame(title = "Strangers on Paper"),
        createGame(title = "Superheroes Suck"),
    )

    private val lastPlayedSorted = listOf(
        createGame(lastPlayed = 0L),
        createGame(lastPlayed = 2L),
        createGame(lastPlayed = 3L),
        createGame(lastPlayed = 4L),
        createGame(lastPlayed = 5L),
        createGame(lastPlayed = 6L),
    )

    private val addedSorted = listOf(
        createGame(added = 0L),
        createGame(added = 2L),
        createGame(added = 3L),
        createGame(added = 4L),
        createGame(added = 5L),
        createGame(added = 6L),
    )

    private val ratingSorted = listOf(
        createGame(rating = 0),
        createGame(rating = 2),
        createGame(rating = 3),
        createGame(rating = 4),
        createGame(rating = 5),
        createGame(rating = 6),
    )

    private val playTimeSorted = listOf(
        createGame(playTime = 0),
        createGame(playTime = 2),
        createGame(playTime = 3),
        createGame(playTime = 4),
        createGame(playTime = 5),
        createGame(playTime = 6),
    )

    private val updateAvailableSorted = listOf(
        createGame(updateAvailable = false),
        createGame(updateAvailable = false),
        createGame(updateAvailable = true),
        createGame(updateAvailable = true),
        createGame(updateAvailable = true),
        createGame(updateAvailable = true),
    )

    @Test
    fun `SortOrder#entries returns all entries`() {
        val entries = SortOrder.entries
        assertEquals(7, entries.size)
        assertContains(entries, SortOrder.AZ)
        assertContains(entries, SortOrder.Rating)
        assertContains(entries, SortOrder.Added)
        assertContains(entries, SortOrder.PlayTime)
        assertContains(entries, SortOrder.LastPlayed)
        assertContains(entries, SortOrder.ReleaseDate)
        assertContains(entries, SortOrder.UpdateAvailable)
    }

    @Test
    fun `SortOrder#AZ SortDirection#Ascending`() {
        val sorted = SortOrder.AZ.sort(titlesSorted.shuffled(), SortDirection.Ascending)
        assertContentEquals(titlesSorted, sorted)
    }

    @Test
    fun `SortOrder#AZ SortDirection#Descending`() {
        val sorted = SortOrder.AZ.sort(titlesSorted.shuffled(), SortDirection.Descending)
        assertContentEquals(titlesSorted.reversed(), sorted)
    }

    @Test
    fun `SortOrder#LastPlayed SortDirection#Ascending`() {
        val sorted = SortOrder.LastPlayed.sort(lastPlayedSorted.shuffled(), SortDirection.Ascending)
        assertContentEquals(lastPlayedSorted, sorted)
    }

    @Test
    fun `SortOrder#LastPlayed SortDirection#Descending`() {
        val sorted = SortOrder.LastPlayed.sort(lastPlayedSorted.shuffled(), SortDirection.Descending)
        assertContentEquals(lastPlayedSorted.reversed(), sorted)
    }

    @Test
    fun `SortOrder#Added SortDirection#Ascending`() {
        val sorted = SortOrder.Added.sort(addedSorted.shuffled(), SortDirection.Ascending)
        assertContentEquals(addedSorted, sorted)
    }

    @Test
    fun `SortOrder#Added SortDirection#Descending`() {
        val sorted = SortOrder.Added.sort(addedSorted.shuffled(), SortDirection.Descending)
        assertContentEquals(addedSorted.reversed(), sorted)
    }

    @Test
    fun `SortOrder#Rating SortDirection#Ascending`() {
        val sorted = SortOrder.Rating.sort(ratingSorted.shuffled(), SortDirection.Ascending)
        assertContentEquals(ratingSorted, sorted)
    }

    @Test
    fun `SortOrder#Rating SortDirection#Descending`() {
        val sorted = SortOrder.Rating.sort(ratingSorted.shuffled(), SortDirection.Descending)
        assertContentEquals(ratingSorted.reversed(), sorted)
    }

    @Test
    fun `SortOrder#PlayTime SortDirection#Ascending`() {
        val sorted = SortOrder.PlayTime.sort(playTimeSorted.shuffled(), SortDirection.Ascending)
        assertContentEquals(playTimeSorted, sorted)
    }

    @Test
    fun `SortOrder#PlayTime SortDirection#Descending`() {
        val sorted = SortOrder.PlayTime.sort(playTimeSorted.shuffled(), SortDirection.Descending)
        assertContentEquals(playTimeSorted.reversed(), sorted)
    }

    @Test
    fun `SortOrder#UpdateAvailable SortDirection#Ascending`() {
        val sorted = SortOrder.UpdateAvailable.sort(updateAvailableSorted.shuffled(), SortDirection.Ascending)
        assertContentEquals(updateAvailableSorted, sorted)
    }

    @Test
    fun `SortOrder#UpdateAvailable SortDirection#Descending`() {
        val sorted = SortOrder.UpdateAvailable.sort(updateAvailableSorted.shuffled(), SortDirection.Descending)
        assertContentEquals(updateAvailableSorted.reversed(), sorted)
    }

    private fun createGame(
        title: String = "",
        lastPlayed: Long = 0L,
        added: Long = 0L,
        rating: Int = 0,
        playTime: Long = 0L,
        updateAvailable: Boolean = false,
        releaseDate: Long = 0L,
    ): Game {
        return Game(
            title, "", "", 0, emptySet(), "", playTime, rating, 0f,
            updateAvailable, added, lastPlayed, 0L, false, releaseDate, 0L,
            PlayState.Playing, null, emptySet(), false, 0L, null, false,
        )
    }
}
