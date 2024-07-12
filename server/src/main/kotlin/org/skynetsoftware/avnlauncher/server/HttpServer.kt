package org.skynetsoftware.avnlauncher.server

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import org.skynetsoftware.avnlauncher.config.Config
import org.skynetsoftware.avnlauncher.domain.coroutines.CoroutineDispatchers
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository
import org.skynetsoftware.avnlauncher.domain.usecase.ImportGameUseCase
import org.skynetsoftware.avnlauncher.domain.utils.Result
import org.skynetsoftware.avnlauncher.logger.Logger
import org.skynetsoftware.avnlauncher.server.dto.GameDto
import org.skynetsoftware.avnlauncher.server.dto.Internal
import org.skynetsoftware.avnlauncher.server.dto.InvalidInput
import org.skynetsoftware.avnlauncher.server.dto.NotFound
import org.skynetsoftware.avnlauncher.server.dto.toGameDto

interface HttpServer {
    fun start()

    fun stop()
}

internal class HttpServerImplementation(
    private val logger: Logger,
    private val coroutineDispatchers: CoroutineDispatchers,
) : HttpServer {
    private var applicationEngine: ApplicationEngine? = null

    override fun start() {
        logger.info("starting http server")
        applicationEngine = embeddedServer(
            factory = Netty,
            port = Config.Defaults.SERVER_PORT,
            host = Config.Defaults.SERVER_HOST,
            module = Application::module,
        )
        applicationEngine?.start(false)
    }

    /**
     * Stop http server gracefully
     * @param gracePeriodMillis maximum time to wait for server to stop gracefully.
     * */
    override fun stop() {
        logger.info("stopping http server")
        applicationEngine?.stop(gracePeriodMillis = 0L)
        applicationEngine = null
    }
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    install(DefaultHeaders) {
        header(HttpHeaders.AccessControlAllowOrigin, "*")
    }

    val gamesRepository by inject<GamesRepository>()
    val importGame by inject<ImportGameUseCase>()

    routing {
        get("/game/{gameId}") {
            val gameId = call.parameters["gameId"]?.toIntOrNull()
            if (gameId == null) {
                call.respond(HttpStatusCode.BadRequest, InvalidInput("gameId is invalid"))
                return@get
            }
            val gameDto: GameDto? = gamesRepository.get(gameId)?.toGameDto()
            if (gameDto == null) {
                call.respond(HttpStatusCode.NotFound, NotFound("game not found"))
                return@get
            }
            call.respond(gameDto)
        }
        post("/game/{gameId}") {
            val gameId = call.parameters["gameId"]
            if (gameId == null) {
                call.respond(HttpStatusCode.BadRequest, InvalidInput("gameId is invalid"))
                return@post
            }
            when (val response = importGame(gameId, null, null)) {
                is Result.Error -> call.respond(
                    HttpStatusCode.InternalServerError,
                    Internal(response.exception.message ?: "Internal Error"),
                )

                is Result.Ok -> call.respondText(text = "", status = HttpStatusCode.Created)
            }
        }
    }
}
