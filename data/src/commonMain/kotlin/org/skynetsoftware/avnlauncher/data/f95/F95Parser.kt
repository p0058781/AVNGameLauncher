package org.skynetsoftware.avnlauncher.data.f95

import io.ktor.client.statement.HttpResponse
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.f95.model.F95Game
import org.skynetsoftware.avnlauncher.domain.utils.Result

internal expect fun Module.f95ParserKoinModule()

internal interface F95Parser {
    suspend fun parseGame(
        httpResponse: HttpResponse,
        gameThreadId: Int,
    ): Result<F95Game>
}
