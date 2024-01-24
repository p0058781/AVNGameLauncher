package org.skynetsoftware.avnlauncher.data.f95

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.prepareGet
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.f95.model.F95Game
import org.skynetsoftware.avnlauncher.domain.utils.Result
import org.skynetsoftware.avnlauncher.logger.Logger

private const val F95_ZONE_BASE_URL = "https://f95zone.to"
private const val REQUEST_TIMEOUT = 60000L

internal fun Module.f95ApiKoinModule() {
    single<F95Api> { F95ApiImpl(get(), get()) }
}

private fun Int.createF95ThreadUrl() = "${F95_ZONE_BASE_URL}/threads/$this"

internal interface F95Api {
    suspend fun getGame(gameThreadId: Int): Result<F95Game>

    suspend fun getGame(gameThreadUrl: String): Result<F95Game>

    suspend fun getRedirectUrl(gameThreadId: Int): Result<String>
}

private class F95ApiImpl(
    private val f95Parser: F95Parser,
    private val logger: Logger,
) : F95Api {
    private val gameThreadUrlRegex = Regex("https://f95zone.to/threads/.+\\.(\\d+)")
    private val httpClient = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }
    private val noRedirectClient = httpClient.config {
        followRedirects = false
    }

    override suspend fun getGame(gameThreadId: Int): Result<F95Game> {
        return try {
            return f95Parser.parseGame(httpClient.get(gameThreadId.createF95ThreadUrl()), gameThreadId)
        } catch (t: Throwable) {
            logger.error(t)
            Result.Error(t)
        }
    }

    override suspend fun getGame(gameThreadUrl: String): Result<F95Game> {
        val threadId = gameThreadUrlRegex.find(gameThreadUrl)?.groups?.get(1)?.value?.toIntOrNull()
        return if (threadId == null) {
            Result.Error(IllegalStateException("Failed to parse gameThreadUrl"))
        } else {
            try {
                return f95Parser.parseGame(httpClient.get(gameThreadUrl), threadId)
            } catch (t: Throwable) {
                logger.error(t)
                Result.Error(t)
            }
        }
    }

    override suspend fun getRedirectUrl(gameThreadId: Int): Result<String> {
        return try {
            val location =
                noRedirectClient.prepareGet(gameThreadId.createF95ThreadUrl()).execute { it.headers["Location"] }
            Result.Ok(location!!)
        } catch (t: Throwable) {
            logger.error(t)
            Result.Error(t)
        }
    }
}
