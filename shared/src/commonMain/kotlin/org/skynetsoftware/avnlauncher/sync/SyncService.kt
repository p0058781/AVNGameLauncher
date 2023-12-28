package org.skynetsoftware.avnlauncher.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.UpdateChecker
import org.skynetsoftware.avnlauncher.data.model.toSyncGame
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository

val syncServiceModule = module {
    single<SyncService> {
        val configManager = get<ConfigManager>()
        if (configManager.remoteClientMode) {
            SyncServiceNoOp()
        } else {
            SyncServiceImpl(get(), get(), get())
        }
    }
}

interface SyncService {
    fun start()

    fun stop()
}

private class SyncServiceNoOp : SyncService {
    override fun start() {}

    override fun stop() {}
}

private const val ONE_HOUR_IN_MS = 3_600_000L
// private const val ONE_HOUR_IN_MS = 60_000L

private class SyncServiceImpl(
    private val gamesRepository: GamesRepository,
    private val syncApi: SyncApi,
    private val updateChecker: UpdateChecker,
) : SyncService {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var syncJob: Job? = null

    override fun start() {
        syncJob?.cancel()
        syncJob = scope.launch {
            sync()
            while (true) {
                delay(ONE_HOUR_IN_MS)
                sync()
            }
        }
    }

    override fun stop() {
        syncJob?.cancel()
    }

    private suspend fun sync() {
        updateChecker.startUpdateCheck(scope, true)
        val allGames = gamesRepository.all().map { it.toSyncGame() }
        syncApi.set(allGames)
    }
}
