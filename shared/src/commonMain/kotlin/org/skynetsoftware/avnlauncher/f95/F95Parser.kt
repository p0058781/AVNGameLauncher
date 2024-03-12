package org.skynetsoftware.avnlauncher.f95

import io.ktor.client.statement.*
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.f95.model.F95Game


expect val f95ParserKoinModule: Module
interface F95Parser {
    suspend fun parseGame(httpResponse: HttpResponse, gameThreadId: Int): Result<F95Game>
}