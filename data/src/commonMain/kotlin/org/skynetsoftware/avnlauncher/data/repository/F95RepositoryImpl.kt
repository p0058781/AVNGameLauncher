package org.skynetsoftware.avnlauncher.data.repository

import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.f95.F95Api
import org.skynetsoftware.avnlauncher.data.mapper.toGame
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.Versions
import org.skynetsoftware.avnlauncher.domain.repository.F95Repository
import org.skynetsoftware.avnlauncher.domain.utils.Result

internal fun Module.f95RepositoryKoinModule() {
    single<F95Repository> { F95RepositoryImpl(get()) }
}

private class F95RepositoryImpl(private val f95Api: F95Api) : F95Repository {
    override suspend fun getGame(gameThreadId: Int): Result<Game> {
        return when (val result = f95Api.getGame(gameThreadId)) {
            is Result.Error -> Result.Error(result.exception)
            is Result.Ok -> Result.Ok(result.value.toGame())
        }
    }

    override suspend fun getVersions(gameThreadIds: List<Int>): Result<Versions> {
        return when (val result = f95Api.getVersions(gameThreadIds)) {
            is Result.Error -> Result.Error(result.exception)
            is Result.Ok -> Result.Ok(result.value.msg)
        }
    }
}
