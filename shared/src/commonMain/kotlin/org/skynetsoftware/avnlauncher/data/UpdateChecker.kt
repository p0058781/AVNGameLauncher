package org.skynetsoftware.avnlauncher.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.model.mergeWith
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.f95.F95Api
import org.skynetsoftware.avnlauncher.logging.Logger
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.settings.SettingsManager
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter

private const val UPDATE_CHECK_INTERVAL = 86_400_000L // 24h

val updateCheckerKoinModule = module {
    single<UpdateChecker> {
        val configManager = get<ConfigManager>()
        if (configManager.remoteClientMode) {
            UpdateCheckerNoOp()
        } else {
            UpdateCheckerImpl(get(), get(), get(), get(), get())
        }
    }
}

interface UpdateChecker {
    data class UpdateResult(
        val game: Game,
        val updateAvailable: Boolean,
        val exception: Throwable?,
    )

    fun startUpdateCheck(
        forceUpdateCheck: Boolean = false,
        onComplete: (updateResults: List<UpdateResult>) -> Unit,
    )

    suspend fun startUpdateCheck(
        scope: CoroutineScope,
        forceUpdateCheck: Boolean = false,
    ): List<UpdateResult>
}

private class UpdateCheckerNoOp : UpdateChecker {
    override fun startUpdateCheck(
        forceUpdateCheck: Boolean,
        onComplete: (updateResults: List<UpdateChecker.UpdateResult>) -> Unit,
    ) {}

    override suspend fun startUpdateCheck(
        scope: CoroutineScope,
        forceUpdateCheck: Boolean,
    ): List<UpdateChecker.UpdateResult> {
        return emptyList()
    }
}

private class UpdateCheckerImpl(
    private val gamesRepository: GamesRepository,
    private val logger: Logger,
    private val f95Api: F95Api,
    private val settingsManager: SettingsManager,
    private val eventCenter: EventCenter,
) : UpdateChecker {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var updateCheckRunning = false

    override fun startUpdateCheck(
        forceUpdateCheck: Boolean,
        onComplete: (updateResults: List<UpdateChecker.UpdateResult>) -> Unit,
    ) {
        scope.launch {
            val result = startUpdateCheck(this, forceUpdateCheck)
            onComplete(result)
        }
    }

    override suspend fun startUpdateCheck(
        scope: CoroutineScope,
        forceUpdateCheck: Boolean,
    ): List<UpdateChecker.UpdateResult> {
        return runIfNotAlreadyRunning {
            eventCenter.emit(Event.UpdateCheckStarted)
            val now = Clock.System.now().toEpochMilliseconds()
            val games = gamesRepository.all()
                .filter { forceUpdateCheck || now > it.lastUpdateCheck + UPDATE_CHECK_INTERVAL }
                .filter { !it.updateAvailable }
                .filter { it.checkForUpdates }
            val fastUpdateCheck = settingsManager.fastUpdateCheck.value
            val updatesResult = games.map { game ->
                scope.async {
                    try {
                        val lastRedirectUrl = game.lastRedirectUrl

                        val result = if (fastUpdateCheck) {
                            val newRedirectUrl = f95Api.getRedirectUrl(game.f95ZoneThreadId).getOrNull()
                            if (newRedirectUrl != lastRedirectUrl) {
                                val slowResult = doSlowUpdateCheck(game)
                                newRedirectUrl?.let { gamesRepository.updateLastRedirectUrl(newRedirectUrl, game) }
                                slowResult
                            } else {
                                UpdateChecker.UpdateResult(game, false, null)
                            }
                        } else {
                            doSlowUpdateCheck(game)
                        }
                        val newGame = result.game.copy(lastUpdateCheck = now)

                        result.copy(game = newGame)
                    } catch (t: Throwable) {
                        logger.error(t)
                        UpdateChecker.UpdateResult(game, false, t)
                    }
                }
            }.map {
                it.await()
            }
            gamesRepository.updateGames(updatesResult.map { it.game })
            if (updatesResult.none { it.updateAvailable }) {
                logger.info("No Updates Available")
            }
            eventCenter.emit(Event.UpdateCheckComplete)
            updatesResult
        }
    }

    private suspend fun doSlowUpdateCheck(game: Game): UpdateChecker.UpdateResult {
        val currentVersion = game.version
        logger.info("doing slow update check for: ${game.title}")
        val f95Game = f95Api.getGame(game.f95ZoneThreadId).getOrThrow()

        var newGame = game.mergeWith(f95Game)

        val newVersion = f95Game.version
        if (newVersion != currentVersion) {
            newGame = newGame.copy(updateAvailable = true, availableVersion = newVersion)
            logger.info("${game.title}: Update Available $newVersion")
        }

        return UpdateChecker.UpdateResult(newGame, newVersion != currentVersion, null)
    }

    private suspend fun runIfNotAlreadyRunning(run: suspend () -> List<UpdateChecker.UpdateResult>): List<UpdateChecker.UpdateResult> {
        if (updateCheckRunning) {
            return emptyList()
        }
        updateCheckRunning = true
        val result = run()
        updateCheckRunning = false
        return result
    }
}

fun List<UpdateChecker.UpdateResult>.buildToastMessage(): String {
    return buildString {
        val updates = this@buildToastMessage.filter { it.updateAvailable }
        if (updates.isNotEmpty()) {
            appendLine(R.strings.toastUpdateAvailable)
            updates.forEach {
                appendLine(it.game.title)
            }
        } else {
            appendLine(R.strings.toastNoUpdatesAvailable)
        }
        val exceptions = this@buildToastMessage.filter { it.exception != null }
        if (exceptions.isNotEmpty()) {
            appendLine(R.strings.toastException)
            exceptions.forEach {
                append(it.game.title)
                append(": ")
                append(it.exception?.message)
            }
        }
    }
}
