package org.skynetsoftware.avnlauncher.f95

import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class F95ApiImplTest: KoinTest {

    private val gameIds = listOf(95537, 77680)

    private val f95Api: F95Api by inject()

    @BeforeTest
    fun beforeTest() {
        startKoin {
            modules(
                f95ApiKoinModule
            )
        }
    }

    @AfterTest
    fun afterTest() {
        stopKoin()
    }

    @Test
    fun `all games from list return Result success`() = runTest {
        gameIds.forEach {
            val result = f95Api.getGame(it)
            if(result.isFailure) {
                result.exceptionOrNull()?.printStackTrace()
            }
            assertTrue(result.isSuccess)
        }
    }
}