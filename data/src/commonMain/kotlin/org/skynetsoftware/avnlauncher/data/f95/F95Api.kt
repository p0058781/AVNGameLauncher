package org.skynetsoftware.avnlauncher.data.f95

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.f95.model.F95Game
import org.skynetsoftware.avnlauncher.data.f95.model.F95Versions
import org.skynetsoftware.avnlauncher.domain.utils.Result
import org.skynetsoftware.avnlauncher.logger.Logger

private const val F95_ZONE_BASE_URL = "https://f95zone.to"
private const val REQUEST_TIMEOUT = 60000L

internal fun Module.f95ApiKoinModule() {
    single<F95Api> { F95ApiImpl(get(), get()) }
}

fun Int.createF95ThreadUrl() = "${F95_ZONE_BASE_URL}/threads/$this"

internal interface F95Api {
    suspend fun getGame(gameThreadId: Int): Result<F95Game>

    suspend fun getVersions(gameThreadIds: List<Int>): Result<F95Versions>
}

private class F95ApiImpl(
    private val f95Parser: F95Parser,
    private val logger: Logger,
) : F95Api {
    private val httpClient = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT
        }
        install(Logging) {
            level = LogLevel.NONE
        }
        install(ContentNegotiation) {
            json()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getGame(gameThreadId: Int): Result<F95Game> {
        return try {
            return f95Parser.parseGame(httpClient.get(gameThreadId.createF95ThreadUrl()), gameThreadId)
        } catch (t: Throwable) {
            logger.error(t)
            Result.Error(t)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getVersions(gameThreadIds: List<Int>): Result<F95Versions> {
        return try {
            val versions: F95Versions =
                httpClient.get("$F95_ZONE_BASE_URL/sam/checker.php?threads=${gameThreadIds.joinToString(",")}").body()
            Result.Ok(versions)
        } catch (t: Throwable) {
            logger.error(t)
            Result.Error(t)
        }
    }
}
