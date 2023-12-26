package org.skynetsoftware.avnlauncher.sync

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository

private const val syncBaseUrl = "https://avnsync.harpi.net"

val syncApiKoinModule = module {
    single<SyncApi> { SyncApiImpl() }
}

interface SyncApi {

    suspend fun get(): List<SyncGame>

    suspend fun set(games: List<SyncGame>): Boolean
}

private class SyncApiImpl : SyncApi {
    private val httpClient = HttpClient {
        defaultRequest {
            url(syncBaseUrl)
            header("Authorization", "G(JWt+Xv,NT?{cQKZZ*C=+eu-by*qj]1M6[]{MpwKTNuX8w;e6f#??E55,#G+,(]")
        }
        install(Logging) {
            level = LogLevel.INFO
        }
        install(ContentNegotiation) {
            json()
        }
    }

    override suspend fun set(games: List<SyncGame>): Boolean {
        return httpClient.post("set") {
            contentType(ContentType.Application.Json)
            setBody(games)
        }.status == HttpStatusCode.OK
    }

    override suspend fun get(): List<SyncGame> {
        val httpResponse = httpClient.get("get")
        return if (httpResponse.status == HttpStatusCode.OK) {
            httpResponse.body()
        } else {
            emptyList()
        }
    }

}