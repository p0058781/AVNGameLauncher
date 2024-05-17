package org.skynetsoftware.avnlauncher.data.repository

import app.cash.sqldelight.Query
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.skynetsoftware.avnlauncher.data.Database
import org.skynetsoftware.avnlauncher.data.GameEntity
import org.skynetsoftware.avnlauncher.data.GameEntityQueries
import org.skynetsoftware.avnlauncher.data.GameEntitySlots
import org.skynetsoftware.avnlauncher.data.GameWithPlaySessions
import org.skynetsoftware.avnlauncher.data.GamesWithPlaySessions
import org.skynetsoftware.avnlauncher.data.mapper.toGame
import org.skynetsoftware.avnlauncher.data.mapper.toGames
import org.skynetsoftware.avnlauncher.domain.model.PlayState
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GamesRepositoryImplTest : KoinTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    private val database = mockk<Database>()
    private val gameEntityQueries = mockk<GameEntityQueries>()
    private val allQuery = mockk<Query<GamesWithPlaySessions>>()

    private lateinit var gameEntitySlots: GameEntitySlots

    private val gamesRepository by inject<GamesRepository>()

    @BeforeTest
    fun setup() {
        gameEntitySlots = GameEntitySlots()

        every { database.gameEntityQueries } returns gameEntityQueries
        every { gameEntityQueries.gamesWithPlaySessions() } returns allQuery

        startKoin {
            modules(
                module {
                    single { database }
                    gamesRepositoryKoinModule(testDispatcher)
                },
            )
        }
    }

    @AfterTest
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `all returns correct value`() =
        runTest {
            val expected = listOf(createRandomGamesWithPlaySessions(), createRandomGamesWithPlaySessions())
            setupGameListMock(expected)
            val games = gamesRepository.all()

            assertEquals(expected.toGames(), games)
        }

    @Test
    fun `get returns correct value`() =
        runTest {
            val expected = listOf(createRandomGameWithPlaySessions())
            setupGetMock(expected[0].f95ZoneThreadId, expected)
            val game = gamesRepository.get(expected[0].f95ZoneThreadId)

            assertEquals(expected.toGame(), game)
        }

    @Test
    fun `get returns null if game doesnt exist`() =
        runTest {
            val expected = emptyList<GameWithPlaySessions>()
            setupGetMock(12345676, expected)
            val game = gamesRepository.get(12345676)

            assertEquals(expected.toGame(), game)
        }

    @Test
    fun `update rating writes correct value`() =
        runTest {
            val expectedId = 1
            val expectedRating = 5

            var actualId = -1
            var actualRating = -1

            every {
                gameEntityQueries.updateRating(
                    capture(gameEntitySlots.rating),
                    capture(gameEntitySlots.f95ZoneThreadId),
                )
            } answers {
                actualId = gameEntitySlots.f95ZoneThreadId.captured
                actualRating = gameEntitySlots.rating.captured
            }

            gamesRepository.updateRating(expectedId, expectedRating)

            verify { gameEntityQueries.updateRating(expectedRating, expectedId) }
            verify { gameEntityQueries.gamesWithPlaySessions() }

            confirmVerified(gameEntityQueries)

            assertEquals(expectedId, actualId)
            assertEquals(expectedRating, actualRating)
        }

    @Test
    fun `insert game writes correct value`() =
        runTest {
            val expectedGame = createRandomGameEntity()

            val gameSlot = slot<GameEntity>()

            var actualGame: GameEntity? = null

            every { gameEntityQueries.insertGame(capture(gameSlot)) } answers {
                actualGame = gameSlot.captured
            }

            gamesRepository.insertGame(expectedGame.toGame())

            verify { gameEntityQueries.insertGame(expectedGame) }
            verify { gameEntityQueries.gamesWithPlaySessions() }

            confirmVerified(gameEntityQueries)

            assertEquals(expectedGame, actualGame)
        }

    @Test
    fun `update games writes correct value`() =
        runTest {
            val expected = createRandomGameEntity()

            var actual: GameEntity? = null

            every {
                gameEntityQueries.updateGame(
                    title = capture(gameEntitySlots.title),
                    imageUrl = capture(gameEntitySlots.imageUrl),
                    customImageUrl = captureNullable(gameEntitySlots.customImageUrl),
                    executablePaths = capture(gameEntitySlots.executablePaths),
                    version = capture(gameEntitySlots.version),
                    rating = capture(gameEntitySlots.rating),
                    f95Rating = capture(gameEntitySlots.f95Rating),
                    updateAvailable = capture(gameEntitySlots.updateAvailable),
                    added = capture(gameEntitySlots.added),
                    hidden = capture(gameEntitySlots.hidden),
                    releaseDate = capture(gameEntitySlots.releaseDate),
                    firstReleaseDate = capture(gameEntitySlots.firstReleaseDate),
                    playState = capture(gameEntitySlots.playState),
                    availableVersion = captureNullable(gameEntitySlots.availableVersion),
                    tags = capture(gameEntitySlots.tags),
                    checkForUpdates = capture(gameEntitySlots.checkForUpdates),
                    notes = captureNullable(gameEntitySlots.notes),
                    favorite = capture(gameEntitySlots.favorite),
                    f95ZoneThreadId = capture(gameEntitySlots.f95ZoneThreadId),
                )
            } answers {
                actual = GameEntity(
                    title = gameEntitySlots.title.captured,
                    imageUrl = gameEntitySlots.imageUrl.captured,
                    customImageUrl = gameEntitySlots.customImageUrl.captured,
                    executablePaths = gameEntitySlots.executablePaths.captured,
                    version = gameEntitySlots.version.captured,
                    playTime = 0L,
                    rating = gameEntitySlots.rating.captured,
                    f95Rating = gameEntitySlots.f95Rating.captured,
                    updateAvailable = gameEntitySlots.updateAvailable.captured,
                    added = gameEntitySlots.added.captured,
                    lastPlayed = 0L,
                    hidden = gameEntitySlots.hidden.captured,
                    releaseDate = gameEntitySlots.releaseDate.captured,
                    firstReleaseDate = gameEntitySlots.firstReleaseDate.captured,
                    playState = gameEntitySlots.playState.captured,
                    availableVersion = gameEntitySlots.availableVersion.captured,
                    tags = gameEntitySlots.tags.captured,
                    checkForUpdates = gameEntitySlots.checkForUpdates.captured,
                    firstPlayed = 0L,
                    notes = gameEntitySlots.notes.captured,
                    favorite = gameEntitySlots.favorite.captured,
                    f95ZoneThreadId = gameEntitySlots.f95ZoneThreadId.captured,
                )
            }

            gamesRepository.updateGames(listOf(expected.toGame()))

            verify {
                gameEntityQueries.updateGame(
                    title = expected.title,
                    imageUrl = expected.imageUrl,
                    customImageUrl = expected.customImageUrl,
                    executablePaths = expected.executablePaths,
                    version = expected.version,
                    rating = expected.rating,
                    f95Rating = expected.f95Rating,
                    updateAvailable = expected.updateAvailable,
                    added = expected.added,
                    hidden = expected.hidden,
                    releaseDate = expected.releaseDate,
                    firstReleaseDate = expected.firstReleaseDate,
                    playState = expected.playState,
                    availableVersion = expected.availableVersion,
                    tags = expected.tags,
                    checkForUpdates = expected.checkForUpdates,
                    notes = expected.notes,
                    favorite = expected.favorite,
                    f95ZoneThreadId = expected.f95ZoneThreadId,
                )
            }
            verify { gameEntityQueries.gamesWithPlaySessions() }

            confirmVerified(gameEntityQueries)

            assertEquals(expected, actual)
        }

    @Test
    fun `updateGame2 writes correct value`() =
        runTest {
            val expected = createRandomGameEntity()

            var executablePaths: Set<String>? = null
            var hidden: Boolean? = null
            var playState: PlayState? = null
            var checkForUpdates: Boolean? = null
            var notes: String? = null
            var f95ZoneThreadId: Int? = null

            every {
                gameEntityQueries.updateGameF95(
                    executablePaths = capture(gameEntitySlots.executablePaths),
                    hidden = capture(gameEntitySlots.hidden),
                    playState = capture(gameEntitySlots.playState),
                    checkForUpdates = capture(gameEntitySlots.checkForUpdates),
                    notes = captureNullable(gameEntitySlots.notes),
                    f95ZoneThreadId = capture(gameEntitySlots.f95ZoneThreadId),
                )
            } answers {
                executablePaths = gameEntitySlots.executablePaths.captured
                hidden = gameEntitySlots.hidden.captured
                playState = gameEntitySlots.playState.captured
                checkForUpdates = gameEntitySlots.checkForUpdates.captured
                notes = gameEntitySlots.notes.captured
                f95ZoneThreadId = gameEntitySlots.f95ZoneThreadId.captured
            }

            gamesRepository.updateGame(
                id = expected.f95ZoneThreadId,
                executablePaths = expected.executablePaths,
                checkForUpdates = expected.checkForUpdates,
                playState = expected.playState,
                hidden = expected.hidden,
                notes = expected.notes,
            )

            verify {
                gameEntityQueries.updateGameF95(
                    executablePaths = expected.executablePaths,
                    hidden = expected.hidden,
                    playState = expected.playState,
                    checkForUpdates = expected.checkForUpdates,
                    notes = expected.notes,
                    f95ZoneThreadId = expected.f95ZoneThreadId,
                )
            }
            verify { gameEntityQueries.gamesWithPlaySessions() }

            confirmVerified(gameEntityQueries)

            assertEquals(expected.executablePaths, executablePaths)
            assertEquals(expected.hidden, hidden)
            assertEquals(expected.playState, playState)
            assertEquals(expected.checkForUpdates, checkForUpdates)
            assertEquals(expected.notes, notes)
            assertEquals(expected.f95ZoneThreadId, f95ZoneThreadId)
        }

    @Test
    fun `updateGame3 writes correct value`() =
        runTest {
            val expected = createRandomGameEntity()

            var title: String? = null
            var imageUrl: String? = null
            var version: String? = null
            var releaseDate: Long? = null
            var firstReleaseDate: Long? = null
            var tags: Set<String>? = null
            var executablePaths: Set<String>? = null
            var hidden: Boolean? = null
            var playState: PlayState? = null
            var checkForUpdates: Boolean? = null
            var notes: String? = null
            var f95ZoneThreadId: Int? = null

            every {
                gameEntityQueries.updateGameNonF95(
                    title = capture(gameEntitySlots.title),
                    imageUrl = capture(gameEntitySlots.imageUrl),
                    version = capture(gameEntitySlots.version),
                    releaseDate = capture(gameEntitySlots.releaseDate),
                    firstReleaseDate = capture(gameEntitySlots.firstReleaseDate),
                    tags = capture(gameEntitySlots.tags),
                    executablePaths = capture(gameEntitySlots.executablePaths),
                    hidden = capture(gameEntitySlots.hidden),
                    playState = capture(gameEntitySlots.playState),
                    checkForUpdates = capture(gameEntitySlots.checkForUpdates),
                    notes = captureNullable(gameEntitySlots.notes),
                    f95ZoneThreadId = capture(gameEntitySlots.f95ZoneThreadId),
                )
            } answers {
                title = gameEntitySlots.title.captured
                imageUrl = gameEntitySlots.imageUrl.captured
                version = gameEntitySlots.version.captured
                releaseDate = gameEntitySlots.releaseDate.captured
                firstReleaseDate = gameEntitySlots.firstReleaseDate.captured
                tags = gameEntitySlots.tags.captured
                executablePaths = gameEntitySlots.executablePaths.captured
                hidden = gameEntitySlots.hidden.captured
                playState = gameEntitySlots.playState.captured
                checkForUpdates = gameEntitySlots.checkForUpdates.captured
                notes = gameEntitySlots.notes.captured
                f95ZoneThreadId = gameEntitySlots.f95ZoneThreadId.captured
            }

            gamesRepository.updateGame(
                id = expected.f95ZoneThreadId,
                title = expected.title,
                imageUrl = expected.imageUrl,
                version = expected.version,
                releaseDate = expected.releaseDate,
                firstReleaseDate = expected.firstReleaseDate,
                tags = expected.tags,
                executablePaths = expected.executablePaths,
                checkForUpdates = expected.checkForUpdates,
                playState = expected.playState,
                hidden = expected.hidden,
                notes = expected.notes,
            )

            verify {
                gameEntityQueries.updateGameNonF95(
                    title = expected.title,
                    imageUrl = expected.imageUrl,
                    version = expected.version,
                    releaseDate = expected.releaseDate,
                    firstReleaseDate = expected.firstReleaseDate,
                    tags = expected.tags,
                    executablePaths = expected.executablePaths,
                    hidden = expected.hidden,
                    playState = expected.playState,
                    checkForUpdates = expected.checkForUpdates,
                    notes = expected.notes,
                    f95ZoneThreadId = expected.f95ZoneThreadId,
                )
            }
            verify { gameEntityQueries.gamesWithPlaySessions() }

            confirmVerified(gameEntityQueries)

            assertEquals(expected.title, title)
            assertEquals(expected.imageUrl, imageUrl)
            assertEquals(expected.releaseDate, releaseDate)
            assertEquals(expected.firstReleaseDate, firstReleaseDate)
            assertEquals(expected.version, version)
            assertEquals(expected.tags, tags)
            assertEquals(expected.executablePaths, executablePaths)
            assertEquals(expected.hidden, hidden)
            assertEquals(expected.playState, playState)
            assertEquals(expected.checkForUpdates, checkForUpdates)
            assertEquals(expected.notes, notes)
            assertEquals(expected.f95ZoneThreadId, f95ZoneThreadId)
        }

    @Test
    fun `updateGame4 writes correct value`() =
        runTest {
            val expected = createRandomGameEntity()

            var version: String? = null
            var updateAvailable: Boolean? = null
            var availableVersion: String? = null
            var f95ZoneThreadId: Int? = null

            every {
                gameEntityQueries.updateVersion(
                    version = capture(gameEntitySlots.version),
                    updateAvailable = capture(gameEntitySlots.updateAvailable),
                    availableVersion = captureNullable(gameEntitySlots.availableVersion),
                    f95ZoneThreadId = capture(gameEntitySlots.f95ZoneThreadId),
                )
            } answers {
                version = gameEntitySlots.version.captured
                updateAvailable = gameEntitySlots.updateAvailable.captured
                availableVersion = gameEntitySlots.availableVersion.captured
                f95ZoneThreadId = gameEntitySlots.f95ZoneThreadId.captured
            }

            gamesRepository.updateGame(
                id = expected.f95ZoneThreadId,
                version = expected.version,
                updateAvailable = expected.updateAvailable,
                availableVersion = expected.availableVersion,
            )

            verify {
                gameEntityQueries.updateVersion(
                    version = expected.version,
                    updateAvailable = expected.updateAvailable,
                    availableVersion = expected.availableVersion,
                    f95ZoneThreadId = expected.f95ZoneThreadId,
                )
            }
            verify { gameEntityQueries.gamesWithPlaySessions() }

            confirmVerified(gameEntityQueries)

            assertEquals(expected.version, version)
            assertEquals(expected.updateAvailable, updateAvailable)
            assertEquals(expected.availableVersion, availableVersion)
            assertEquals(expected.f95ZoneThreadId, f95ZoneThreadId)
        }

    @Test
    fun `updateExecutablePaths writes correct value`() =
        runTest {
            val expected = listOf(
                13423 to setOf(getRandomString(10)),
                123143 to setOf(getRandomString(4)),
            )

            val executablePathsSlotsMap = expected.associate { it.first to slot<Set<String>>() }

            val actual: MutableList<Pair<Int, Set<String>>> = mutableListOf()

            expected.forEach { expectedExecutablePaths ->
                every {
                    gameEntityQueries.updateExecutablePaths(
                        executablePaths = capture(executablePathsSlotsMap[expectedExecutablePaths.first]!!),
                        f95ZoneThreadId = expectedExecutablePaths.first,
                    )
                } answers {
                    actual.add(expectedExecutablePaths.first to executablePathsSlotsMap[expectedExecutablePaths.first]!!.captured)
                }
            }

            gamesRepository.updateExecutablePaths(
                expected,
            )

            expected.forEach {
                verify {
                    gameEntityQueries.updateExecutablePaths(
                        it.second,
                        it.first,
                    )
                }
            }
            verify { gameEntityQueries.gamesWithPlaySessions() }

            confirmVerified(gameEntityQueries)

            assertEquals(expected, actual)
        }

    private fun setupGameListMock(games: List<GamesWithPlaySessions>) {
        every { allQuery.executeAsList() } returns games
    }

    private fun setupGetMock(
        id: Int,
        gameEntity: List<GameWithPlaySessions>,
    ) {
        val getQuery = mockk<Query<GameWithPlaySessions>>()
        every { gameEntityQueries.gameWithPlaySessions(id) } returns getQuery
        every { getQuery.executeAsList() } returns gameEntity
    }

    private fun createRandomGamesWithPlaySessions(): GamesWithPlaySessions {
        return GamesWithPlaySessions(
            f95ZoneThreadId = (0..Int.MAX_VALUE).random(),
            title = getRandomString(10),
            imageUrl = getRandomString(10),
            executablePaths = setOf(getRandomString(3), getRandomString(4)),
            version = getRandomString(5),
            playTime = System.currentTimeMillis(),
            rating = (0..5).random(),
            f95Rating = (0..5).random().toFloat(),
            updateAvailable = (0..1).random() == 1,
            added = System.currentTimeMillis(),
            lastPlayed = System.currentTimeMillis(),
            hidden = (0..1).random() == 1,
            releaseDate = System.currentTimeMillis(),
            firstReleaseDate = System.currentTimeMillis(),
            playState = PlayState.entries[(0 until PlayState.entries.size).random()],
            availableVersion = getRandomString(5),
            tags = setOf(getRandomString(4), getRandomString(3)),
            checkForUpdates = (0..1).random() == 1,
            customImageUrl = null,
            firstPlayed = System.currentTimeMillis(),
            notes = getRandomString(55),
            favorite = (0..1).random() == 1,
            playSessionStartTime = System.currentTimeMillis(),
            playSessionEndTime = System.currentTimeMillis(),
            playSessionVersion = getRandomString(5),
        )
    }

    private fun createRandomGameWithPlaySessions(): GameWithPlaySessions {
        return GameWithPlaySessions(
            f95ZoneThreadId = (0..Int.MAX_VALUE).random(),
            title = getRandomString(10),
            imageUrl = getRandomString(10),
            executablePaths = setOf(getRandomString(3), getRandomString(4)),
            version = getRandomString(5),
            playTime = System.currentTimeMillis(),
            rating = (0..5).random(),
            f95Rating = (0..5).random().toFloat(),
            updateAvailable = (0..1).random() == 1,
            added = System.currentTimeMillis(),
            lastPlayed = System.currentTimeMillis(),
            hidden = (0..1).random() == 1,
            releaseDate = System.currentTimeMillis(),
            firstReleaseDate = System.currentTimeMillis(),
            playState = PlayState.entries[(0 until PlayState.entries.size).random()],
            availableVersion = getRandomString(5),
            tags = setOf(getRandomString(4), getRandomString(3)),
            checkForUpdates = (0..1).random() == 1,
            customImageUrl = null,
            firstPlayed = System.currentTimeMillis(),
            notes = getRandomString(55),
            favorite = (0..1).random() == 1,
            playSessionStartTime = System.currentTimeMillis(),
            playSessionEndTime = System.currentTimeMillis(),
            playSessionVersion = getRandomString(5),
        )
    }

    private fun createRandomGameEntity(): GameEntity {
        return GameEntity(
            f95ZoneThreadId = (0..Int.MAX_VALUE).random(),
            title = getRandomString(10),
            imageUrl = getRandomString(10),
            executablePaths = setOf(getRandomString(3), getRandomString(4)),
            version = getRandomString(5),
            playTime = 0L,
            rating = (0..5).random(),
            f95Rating = (0..5).random().toFloat(),
            updateAvailable = (0..1).random() == 1,
            added = System.currentTimeMillis(),
            lastPlayed = 0L,
            hidden = (0..1).random() == 1,
            releaseDate = System.currentTimeMillis(),
            firstReleaseDate = System.currentTimeMillis(),
            playState = PlayState.entries[(0 until PlayState.entries.size).random()],
            availableVersion = getRandomString(5),
            tags = setOf(getRandomString(4), getRandomString(3)),
            checkForUpdates = (0..1).random() == 1,
            customImageUrl = null,
            firstPlayed = 0L,
            notes = getRandomString(55),
            favorite = (0..1).random() == 1,
        )
    }

    private fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}
