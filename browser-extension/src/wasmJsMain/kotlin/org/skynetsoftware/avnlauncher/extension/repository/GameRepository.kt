package org.skynetsoftware.avnlauncher.extension.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import org.skynetsoftware.avnlauncher.config.Config
import org.skynetsoftware.avnlauncher.extension.model.GameDto

class GameRepository {
    private val gameBaseUrl = "http://${Config.Defaults.SERVER_HOST}:${Config.Defaults.SERVER_PORT}/game/"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getGame(gameId: Int): GetGameResponse {
        return try {
            val response = client.get("$gameBaseUrl$gameId")
            if (response.status == HttpStatusCode.OK) {
                GetGameResponse.Game(response.body<GameDto>())
            } else {
                GetGameResponse.GameNotFound
            }
        } catch (t: Throwable) {
            println(t)
            GetGameResponse.Error(t.message)
        }
    }

    suspend fun addGame(gameId: Int): Boolean {
        return try {
            val response = client.post("$gameBaseUrl$gameId")
            return response.status == HttpStatusCode.Created
        } catch (t: Throwable) {
            println(t)
            false
        }
    }

    sealed class GetGameResponse {
        object GameNotFound : GetGameResponse()

        class Error(val message: String?) : GetGameResponse()

        class Game(val gameDto: GameDto) : GetGameResponse()
    }
}
