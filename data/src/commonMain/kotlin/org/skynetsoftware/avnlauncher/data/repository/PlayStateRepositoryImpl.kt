package org.skynetsoftware.avnlauncher.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.Database
import org.skynetsoftware.avnlauncher.data.PlayStateEntityQueries
import org.skynetsoftware.avnlauncher.data.mapper.toPlayState
import org.skynetsoftware.avnlauncher.data.mapper.toPlayStateEntity
import org.skynetsoftware.avnlauncher.domain.coroutines.CoroutineDispatchers
import org.skynetsoftware.avnlauncher.domain.model.PlayState
import org.skynetsoftware.avnlauncher.domain.repository.PlayStateRepository

internal fun Module.playStateRepositoryKoinModule() {
    single<PlayStateRepository> {
        val database = get<Database>()
        PlayStateRepositoryImpl(database.playStateEntityQueries, get())
    }
}

private class PlayStateRepositoryImpl(
    private val playStateEntityQueries: PlayStateEntityQueries,
    private val coroutineDispatchers: CoroutineDispatchers,
) : PlayStateRepository {
    override val playStates: Flow<List<PlayState>> =
        playStateEntityQueries.playStates().asFlow().mapToList(coroutineDispatchers.io)
            .map { it.map { it.toPlayState() } }

    override fun getById(id: String): PlayState? {
        return playStateEntityQueries.getById(id).executeAsOneOrNull()?.toPlayState()
    }

    override fun getByLabel(label: String): PlayState? {
        return playStateEntityQueries.getByLabel(label).executeAsOneOrNull()?.toPlayState()
    }

    override fun insert(playState: PlayState) {
        playStateEntityQueries.insert(playState.toPlayStateEntity())
    }

    override fun update(
        id: String,
        label: String,
        description: String?,
    ) {
        playStateEntityQueries.update(label, description, id)
    }

    override fun delete(id: String) {
        playStateEntityQueries.delete(id)
    }
}
