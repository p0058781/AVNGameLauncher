package org.skynetsoftware.avnlauncher.data.f95

import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.skynetsoftware.avnlauncher.config.Config
import org.skynetsoftware.avnlauncher.data.dataKoinModule
import org.skynetsoftware.avnlauncher.domain.utils.Result
import org.skynetsoftware.avnlauncher.logger.Logger
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class F95ApiTest : KoinTest {
    // 1904, 25081 - 404 if not logged in

    /*private val gameIds = listOf(
        75232, 79512, 49572, 9242, 62625, 38131, 53676, 25739, 12818, 79758, 107102, 68105,
        25332, 33797, 79740, 85108, 119199, 42668, 115436, 86797, 56677, 29981, 49330, 18063, 48734, 12181, 112212,
        52337, 34378, 52621, 20766, 36610, 4907, 18909, 3222, 10668, 17836, 7875, 13673, 14093, 18207, 10672,
        36263, 10120, 13009, 128289, 64576, 100968, 125510, 72109, 37164, 90112, 33607, 35119, 106495, 34351,
        85466, 20072, 55165, 82861, 45993, 95356, 34331, 40217, 71041, 89295, 52669, 35910, 15081, 129528, 154426,
        101751, 107137, 31351, 115549, 127763, 117916, 45466, 52432, 112500, 158858, 106028, 33424, 28008, 87472,
        97715, 85755, 114874, 113727, 5291, 6996, 42223, 93340, 37470, 99680, 85276, 136622, 142559, 97989,
        87315, 143472, 84193, 49245, 50772, 69735, 85730, 137720, 70133, 88824, 102771, 80788, 45616, 112700, 53556,
        70122, 71348, 126667, 53295, 74452, 72891, 96829, 11314, 27221, 88746, 597, 163416, 138468, 163496, 97295,
        99159, 85280, 78309, 125534, 45004, 55994, 106380, 113520, 91617, 82710, 111945, 67104, 77680, 95537,
        66753, 35068, 50840, 118423, 58369, 10361, 133769, 103587, 161185, 143959, 146412, 161093, 65970, 130096,
        90033, 87477, 163962, 158551, 143045, 164483, 126219, 140107, 58555, 135723, 16569, 140310, 147103, 125324,
        110808, 66912, 93557, 135123, 25264, 23184
    )*/
    private val gameIds = listOf(
        75232,
        79512,
        49572,
        9242,
        62625,
        45993,
        23184,
    )

    private val config = Config(System.getProperty("java.io.tmpdir")!!, System.getProperty("java.io.tmpdir")!!)

    private val f95Api: F95Api by inject()

    @BeforeTest
    fun beforeTest() {
        startKoin {
            modules(
                dataKoinModule,
                module {
                    single { config }
                    single<Logger> { mockk<Logger>() }
                },
            )
        }
    }

    @AfterTest
    fun afterTest() {
        stopKoin()
    }

    @Test
    fun `all games from list return Result success`() =
        runBlocking {
            listOf(161013).forEach {
                val result = f95Api.getGame(it)
                if (result is Result.Error) {
                    println(it)
                    result.exception.printStackTrace()
                }
                assertTrue(result is Result.Ok)
            }
        }

    @Test
    fun `getGame with invalid gameThreadId returns Result failure`() =
        runTest {
            val result = f95Api.getGame(1234567)
            if (result is Result.Error) {
                result.exception.printStackTrace()
            }
            assertTrue(result is Result.Error)
        }

    @Test
    fun `getGame returns valid imageUrl`() =
        runBlocking {
            gameIds.forEach {
                val result = f95Api.getGame(it)
                when (result) {
                    is Result.Error -> {
                        println(it)
                        result.exception.printStackTrace()
                    }
                    is Result.Ok -> {
                        assertTrue(result.value.imageUrl.toHttpUrlOrNull() != null)
                    }
                }
                assertTrue(result is Result.Ok)
            }
        }
}
