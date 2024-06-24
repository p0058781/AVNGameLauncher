package org.skynetsoftware.avnlauncher.domain.model

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class FilterTest {
    private val games = listOf(
        createGame(PlayState.Playing, false, true, 3600_001L),
        createGame(PlayState.Playing, false, false, 4567),
        createGame(PlayState.Completed, true, true, 39600_001L),
        createGame(PlayState.Completed, false, true, 39600_001L),
        createGame(PlayState.WaitingForUpdate, false, false, 0),
        createGame(PlayState.WaitingForUpdate, true, false, 0),
    )

    @Test
    fun `entries returns all values`() {
        val entries = Filter.entries
        assertEquals(9, entries.size)
        assertContains(entries, Filter.All)
        assertContains(entries, Filter.Playing)
        assertContains(entries, Filter.NotStarted)
        assertContains(entries, Filter.Completed)
        assertContains(entries, Filter.WaitingForUpdate)
        assertContains(entries, Filter.GamesWithUpdate)
        assertContains(entries, Filter.HiddenGames)
        assertContains(entries, Filter.UnplayedGames)
        assertContains(entries, Filter.Favorites)
    }

    @Test
    fun `Filter_All_filter returns all games except hidden`() {
        val all = Filter.All
        assertEquals(4, all.filter(games).size)
    }

    @Test
    fun `Filter_GamesWithUpdate_filter returns all games with update except hidden`() {
        val all = Filter.GamesWithUpdate
        assertEquals(2, all.filter(games).size)
    }

    @Test
    fun `Filter_HiddenGames_filter returns all games hidden games`() {
        val all = Filter.HiddenGames
        assertEquals(2, all.filter(games).size)
    }

    @Test
    fun `Filter_UnplayedGames_filter returns all unplayed games`() {
        val all = Filter.UnplayedGames
        assertEquals(2, all.filter(games).size)
    }

    @Test
    fun `Filter_Playing_filter returns all playing games`() {
        val all = Filter.Playing
        assertEquals(2, all.filter(games).size)
    }

    @Test
    fun `Filter_Completed_filter returns all completed games`() {
        val all = Filter.Completed
        assertEquals(1, all.filter(games).size)
    }

    @Test
    fun `Filter_WaitingForUpdate_filter returns all waiting for update games`() {
        val all = Filter.WaitingForUpdate
        assertEquals(1, all.filter(games).size)
    }

    private fun createGame(
        playState: PlayState,
        hidden: Boolean,
        updateAvailable: Boolean,
        playTime: Long,
    ): Game {
        return Game(
            title = "",
            description = "",
            developer = "",
            imageUrl = "",
            f95ZoneThreadId = 0,
            executablePaths = emptySet(),
            version = "",
            rating = 0,
            f95Rating = 0f,
            updateAvailable = updateAvailable,
            added = 0L,
            hidden = hidden,
            releaseDate = 0L,
            firstReleaseDate = 0L,
            playState = playState,
            availableVersion = null,
            tags = emptySet(),
            checkForUpdates = false,
            notes = null,
            favorite = false,
            playSessions = emptyList(),
            totalPlayTime = playTime,
            firstPlayedTime = 0L,
            lastPlayedTime = 0L,
        )
    }
}
