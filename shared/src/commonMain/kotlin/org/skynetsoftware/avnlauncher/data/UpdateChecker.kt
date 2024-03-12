package org.skynetsoftware.avnlauncher.data

import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.database.model.RealmGame
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.jsoup.Jsoup
import org.skynetsoftware.avnlauncher.logging.Logger

private const val UPDATE_CHECK_INTERVAL = 86_400_000L//24h

val updateCheckerKoinModule = module {
    single<UpdateChecker> { UpdateCheckerImpl(get(), get()) }
}

interface UpdateChecker {

    class UpdateResult(
        val game: RealmGame,
        val updateAvailable: Boolean,
        val exception: Exception?
    )

    fun startUpdateCheck(forceUpdateCheck: Boolean = false, onComplete: (updateResults: List<UpdateResult>) -> Unit)
}

private class UpdateCheckerImpl(private val gamesRepository: GamesRepository, private val logger: Logger) : UpdateChecker {

    private val titleRegex = Regex("(.+)\\s*\\[(.+)\\]\\s*\\[(.+)\\]")
    private val releaseDateRegex = Regex("<b>Release Date</b>: (.*)")

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var updateCheckRunning = false

    override fun startUpdateCheck(
        forceUpdateCheck: Boolean,
        onComplete: (updateResults: List<UpdateChecker.UpdateResult>) -> Unit
    ) {
        scope.launch {
            runIfNotAlreadyRunning {
                val now = Clock.System.now().toEpochMilliseconds()
                val games = gamesRepository.all()
                    .filter { forceUpdateCheck || now > it.lastUpdateCheck + UPDATE_CHECK_INTERVAL }
                    .filter { !it.updateAvailable }
                val updatesResult = games.map { game ->
                    async {
                        try {
                            val currentVersion = game.version
                            val (newVersion, releaseDate) = getNewVersionAndReleaseDate(game)
                            if (newVersion != currentVersion) {
                                gamesRepository.updateUpdateAvailable(true, game)
                                newVersion?.let { gamesRepository.updateAvailableVersion(newVersion, game) }
                                logger.info("${game.title}: Update Available $newVersion")
                            }
                            releaseDate?.let {
                                gamesRepository.updateReleaseDate(it, game)
                            }
                            gamesRepository.updateLastUpdateCheck(now, game)
                            UpdateChecker.UpdateResult(game, newVersion != currentVersion, null)
                        } catch (e: Exception) {
                            logger.error(e)
                            UpdateChecker.UpdateResult(game, false, e)
                        }
                    }
                }.map { it.await() }
                if (updatesResult.none { it.updateAvailable }) {
                    logger.info("No Updates Available")
                }
                onComplete(updatesResult)
            }
        }
    }

    private fun getNewVersionAndReleaseDate(game: RealmGame): Pair<String?, String?> {
        val document = Jsoup.connect(game.f95ZoneUrl).get()
        val titleRaw = document.select(".p-title-value").first()?.textNodes()?.first()?.text()
        val bbWrapper = document.select(".bbWrapper").first()?.html()

        val matchResult = titleRaw?.let { titleRegex.matchEntire(it) }
        val version = matchResult?.groups?.get(2)?.value?.trim()

        val releaseDate = bbWrapper?.let { releaseDateRegex.find(bbWrapper)?.groups?.get(1)?.value }

        return version to releaseDate
    }

    private suspend fun runIfNotAlreadyRunning(run: suspend () -> Unit) {
        if (updateCheckRunning) {
            return
        }
        updateCheckRunning = true
        run()
        updateCheckRunning = false
    }
}