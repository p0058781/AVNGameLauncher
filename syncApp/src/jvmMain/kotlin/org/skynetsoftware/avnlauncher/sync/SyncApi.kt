package org.skynetsoftware.avnlauncher.sync

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module

private const val SYNC_BASE_URL = "https://avnsync.harpi.net"

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
            url(SYNC_BASE_URL)
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
