package org.skynetsoftware.avnlauncher.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.Database
import org.skynetsoftware.avnlauncher.data.PlaySessionEntityQueries
import org.skynetsoftware.avnlauncher.domain.model.PlaySession
import org.skynetsoftware.avnlauncher.domain.repository.PlaySessionRepository

internal fun Module.playSessionRepositoryKoinModule(coroutineDispatcher: CoroutineDispatcher) {
    single<PlaySessionRepository> {
        PlaySessionRepositoryImpl(get<Database>().playSessionEntityQueries, coroutineDispatcher)
    }
}

class PlaySessionRepositoryImpl(
    private val playSessionEntityQueries: PlaySessionEntityQueries,
    private val coroutineDispatcher: CoroutineDispatcher,
) : PlaySessionRepository {
    override suspend fun insertPlaySession(playSession: PlaySession) {
        withContext(coroutineDispatcher) {
            playSessionEntityQueries.insert(
                gameId = playSession.gameId,
                startTime = playSession.startTime,
                endTime = playSession.endTime,
                version = playSession.version,
            )
        }
    }
}
