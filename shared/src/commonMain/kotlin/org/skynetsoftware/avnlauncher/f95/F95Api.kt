package org.skynetsoftware.avnlauncher.f95

import com.russhwolf.settings.Settings
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.f95.model.F95Game

const val f95ZoneThreadBaseUrl = "https://f95zone.to/threads/"

val f95ApiKoinModule = module {
    single<F95Api> { F95ApiImpl(get(), F95CookiesStorage(Settings())) }
}

fun Int.createF95ThreadUrl() = "$f95ZoneThreadBaseUrl$this"

interface F95Api {
    suspend fun getGame(gameThreadId: Int): Result<F95Game>
    suspend fun getGame(gameThreadUrl: String): Result<F95Game>
}

private class F95ApiImpl(private val f95Parser: F95Parser, private val f95CookiesStorage: F95CookiesStorage) : F95Api {
    private val gameThreadUrlRegex = Regex("https://f95zone.to/threads/.+\\.(\\d+)")
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

    override suspend fun getGame(gameThreadId: Int): Result<F95Game> {
        return f95Parser.parseGame(httpClient.get(gameThreadId.createF95ThreadUrl()), gameThreadId)
    }

    override suspend fun getGame(gameThreadUrl: String): Result<F95Game> {
        val threadId = gameThreadUrlRegex.find(gameThreadUrl)?.groups?.get(1)?.value?.toIntOrNull()
        return if(threadId == null) {
            Result.failure(IllegalStateException("Failed to parse gameThreadUrl"))
        } else {
            getGame(threadId)
        }
    }

}