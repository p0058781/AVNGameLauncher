package org.skynetsoftware.avnlauncher.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.UpdateChecker
import org.skynetsoftware.avnlauncher.data.model.toSyncGame
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.logging.Logger
import org.skynetsoftware.avnlauncher.settings.SettingsManager
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter
import kotlin.math.max

// TODO sync needs to be moved to different module
val syncServiceModule = module {
    single<SyncService> {
        SyncServiceImpl(get(), get(), get(), get(), get(), get())
    }
}

interface SyncService {
    fun start()

    fun stop()
}

private const val MAX_SYNC_INTERVAL = 3_600_000L

private class SyncServiceImpl(
    private val gamesRepository: GamesRepository,
    private val syncApi: SyncApi,
    private val updateChecker: UpdateChecker,
    private val settingsManager: SettingsManager,
    private val eventCenter: EventCenter,
    private val logger: Logger,
) : SyncService {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var syncJob: Job? = null

    override fun start() {
        logger.info("starting sync service")
        syncJob?.cancel()
        syncJob = scope.launch {
            while (true) {
                val now = Clock.System.now().toEpochMilliseconds()
                val lastSyncTime = settingsManager.lastSyncTime.value

                var lastSyncElapsedTime = now - lastSyncTime

                if (lastSyncElapsedTime > MAX_SYNC_INTERVAL) {
                    sync()
                    settingsManager.setLastSyncTime(now)
                    lastSyncElapsedTime = 0
                }

                val delay = MAX_SYNC_INTERVAL - max(0, lastSyncElapsedTime)
                delay(delay)
                sync()
                settingsManager.setLastSyncTime(Clock.System.now().toEpochMilliseconds())
            }
        }
    }

    override fun stop() {
        logger.info("stopping sync")
        syncJob?.cancel()
    }

    private suspend fun sync() {
        logger.info("sync in progress")
        eventCenter.emit(Event.SyncStarted)
        updateChecker.startUpdateCheck(scope, true)
        val allGames = gamesRepository.all().map { it.toSyncGame() }
        syncApi.set(allGames)
        eventCenter.emit(Event.SyncCompleted)
        logger.info("sync done")
    }
}
