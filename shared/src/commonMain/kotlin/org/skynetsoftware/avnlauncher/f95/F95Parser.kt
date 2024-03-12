package org.skynetsoftware.avnlauncher.f95

import io.ktor.client.statement.HttpResponse
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.f95.model.F95Game
import org.skynetsoftware.avnlauncher.utils.Result

expect val f95ParserKoinModule: Module

interface F95Parser {
    suspend fun parseGame(
        httpResponse: HttpResponse,
        gameThreadId: Int,
    ): Result<F95Game>
}
