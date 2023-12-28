package org.skynetsoftware.avnlauncher.f95

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.prepareGet
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.f95.model.F95Game

const val F95_ZONE_THREAD_BASE_URL = "https://f95zone.to/threads/"

val f95ApiKoinModule = module {
    single<F95Api> { F95ApiImpl(get(), F95CookiesStorage(Settings())) }
}

fun Int.createF95ThreadUrl() = "$F95_ZONE_THREAD_BASE_URL$this"

interface F95Api {
    suspend fun login(
        username: String,
        password: String,
    ): Result<Unit>

    suspend fun getGame(gameThreadId: Int): Result<F95Game>

    suspend fun getGame(gameThreadUrl: String): Result<F95Game>

    suspend fun getRedirectUrl(gameThreadId: Int): Result<String>
}

private class F95ApiImpl(private val f95Parser: F95Parser, private val f95CookiesStorage: F95CookiesStorage) : F95Api {
    private val gameThreadUrlRegex = Regex("https://f95zone.to/threads/.+\\.(\\d+)")
    private val loginPageUrl = "https://f95zone.to/login/"
    private val httpClient = HttpClient {
        /*install(HttpCookies) {
            storage = f95CookiesStorage
        }*/
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
        }
        install(Logging) {
            level = LogLevel.NONE
        }
    }
    private val noRedirectClient = httpClient.config {
        followRedirects = false
    }

    override suspend fun login(
        username: String,
        password: String,
    ): Result<Unit> {
        TODO("not implemented")
    }

    override suspend fun getGame(gameThreadId: Int): Result<F95Game> {
        return f95Parser.parseGame(httpClient.get(gameThreadId.createF95ThreadUrl()), gameThreadId)
    }

    override suspend fun getGame(gameThreadUrl: String): Result<F95Game> {
        val threadId = gameThreadUrlRegex.find(gameThreadUrl)?.groups?.get(1)?.value?.toIntOrNull()
        return if (threadId == null) {
            Result.failure(IllegalStateException("Failed to parse gameThreadUrl"))
        } else {
            return f95Parser.parseGame(httpClient.get(gameThreadUrl), threadId)
        }
    }

    override suspend fun getRedirectUrl(gameThreadId: Int): Result<String> {
        return try {
            val location = noRedirectClient.prepareGet(gameThreadId.createF95ThreadUrl()).execute { it.headers["Location"] }
            Result.success(location!!)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}
