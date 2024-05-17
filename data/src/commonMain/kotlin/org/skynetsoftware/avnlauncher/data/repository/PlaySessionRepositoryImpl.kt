package org.skynetsoftware.avnlauncher.data.repository

import kotlinx.coroutines.withContext
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.Database
import org.skynetsoftware.avnlauncher.data.PlaySessionEntityQueries
import org.skynetsoftware.avnlauncher.domain.coroutines.CoroutineDispatchers
import org.skynetsoftware.avnlauncher.domain.model.PlaySession
import org.skynetsoftware.avnlauncher.domain.repository.PlaySessionRepository

internal fun Module.playSessionRepositoryKoinModule() {
    single<PlaySessionRepository> {
        PlaySessionRepositoryImpl(get<Database>().playSessionEntityQueries, get())
    }
}

class PlaySessionRepositoryImpl(
    private val playSessionEntityQueries: PlaySessionEntityQueries,
    private val coroutineDispatchers: CoroutineDispatchers,
) : PlaySessionRepository {
    override suspend fun insertPlaySession(playSession: PlaySession) {
        withContext(coroutineDispatchers.io) {
            playSessionEntityQueries.insert(
                gameId = playSession.gameId,
                startTime = playSession.startTime,
                endTime = playSession.endTime,
                version = playSession.version,
            )
        }
    }
}
