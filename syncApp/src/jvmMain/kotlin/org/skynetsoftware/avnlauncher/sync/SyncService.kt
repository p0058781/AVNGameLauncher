package org.skynetsoftware.avnlauncher.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import kotlin.math.max

val syncServiceModule = module {
    single<SyncService> {
        SyncServiceImpl(get(), get(), get())
    }
}

interface SyncService {
    enum class State {
        Idle,
        Stopped,
        Stopping,
        Syncing,
    }

    val state: StateFlow<State>

    fun start()

    fun stop()
}

private const val MAX_SYNC_INTERVAL = 3_600_000L

private class SyncServiceImpl(
    private val gamesRepository: GamesRepository,
    private val syncApi: SyncApi,
    private val settingsRepository: SettingsRepository,
) : SyncService {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var syncJob: Job? = null

    private val _state = MutableStateFlow(SyncService.State.Stopped)
    override val state get() = _state

    override fun start() {
        syncJob?.cancel()
        syncJob = scope.launch {
            state.emit(SyncService.State.Idle)
            while (isActive) {
                val now = System.currentTimeMillis()
                val lastSyncTime = settingsRepository.lastSyncTime.value

                var lastSyncElapsedTime = now - lastSyncTime

                if (lastSyncElapsedTime > MAX_SYNC_INTERVAL) {
                    sync()
                    settingsRepository.setLastSyncTime(now)
                    lastSyncElapsedTime = 0
                }

                val delay = MAX_SYNC_INTERVAL - max(0, lastSyncElapsedTime)
                delay(delay)
                sync()
                settingsRepository.setLastSyncTime(System.currentTimeMillis())
            }
            state.emit(SyncService.State.Stopped)
        }
    }

    override fun stop() {
        scope.launch {
            state.emit(SyncService.State.Stopping)
        }
        syncJob?.cancel()
    }

    private suspend fun sync() {
        state.emit(SyncService.State.Syncing)
        val allGames = gamesRepository.all().map { it.toSyncGame() }
        syncApi.set(allGames)
        state.emit(SyncService.State.Idle)
    }
}
