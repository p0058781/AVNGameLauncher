package org.skynetsoftware.avnlauncher.sync

import kotlinx.coroutines.*
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.UpdateChecker
import org.skynetsoftware.avnlauncher.data.model.toSyncGame
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.settings.SettingsManager

val syncServiceModule = module {
    single<SyncService> { SyncServiceImpl(get(), get(), get(), get()) }
}

interface SyncService {
    fun start()
    fun stop()
}

private const val ONE_HOUR_IN_MS = 3_600_000L

private class SyncServiceImpl(
    private val gamesRepository: GamesRepository,
    private val syncApi: SyncApi,
    private val updateChecker: UpdateChecker,
    private val settingsManager: SettingsManager
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
        if(settingsManager.remoteClientMode.value) {
            return
        }
        updateChecker.startUpdateCheck(scope, true)
        val allGames = gamesRepository.all().map { it.toSyncGame() }
        syncApi.set(allGames)
    }

}