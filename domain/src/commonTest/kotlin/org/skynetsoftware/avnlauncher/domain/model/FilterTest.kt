package org.skynetsoftware.avnlauncher.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class FilterTest {
    private val playStatePlaying = PlayState(
        id = "Playing",
        label = "Playing",
        description = null,
    )

    private val playStateCompleted = PlayState(
        id = "Completed",
        label = "Completed",
        description = null,
    )

    private val playStateWaitingForUpdate = PlayState(
        id = "WaitingForUpdate",
        label = "WaitingForUpdate",
        description = null,
    )

    private val games = listOf(
        createGame(playStatePlaying, false, true, 3600_001L),
        createGame(playStatePlaying, false, false, 4567),
        createGame(playStateCompleted, true, true, 39600_001L),
        createGame(playStateCompleted, false, true, 39600_001L),
        createGame(playStateWaitingForUpdate, false, false, 0),
        createGame(playStateWaitingForUpdate, true, false, 0),
    )

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
        val all = Filter.PlayState(playStatePlaying.id)
        assertEquals(2, all.filter(games).size)
    }

    @Test
    fun `Filter_Completed_filter returns all completed games`() {
        val all = Filter.PlayState(playStateCompleted.id)
        assertEquals(1, all.filter(games).size)
    }

    @Test
    fun `Filter_WaitingForUpdate_filter returns all waiting for update games`() {
        val all = Filter.PlayState(playStateWaitingForUpdate.id)
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
            prefixes = emptySet(),
            checkForUpdates = false,
            notes = null,
            playSessions = emptyList(),
            lists = emptyList(),
            totalPlayTime = playTime,
            firstPlayedTime = 0L,
            lastPlayedTime = 0L,
        )
    }
}
