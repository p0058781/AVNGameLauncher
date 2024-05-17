package org.skynetsoftware.avnlauncher.ui.screen.game

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.skynetsoftware.avnlauncher.domain.coroutines.CoroutineDispatchers
import org.skynetsoftware.avnlauncher.domain.model.GameWithStats
import org.skynetsoftware.avnlauncher.domain.model.PlaySession
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.utils.calculateAveragePlayTime
import org.skynetsoftware.avnlauncher.utils.isTimeBetween
import org.skynetsoftware.avnlauncher.utils.minus1MonthMillis
import org.skynetsoftware.avnlauncher.utils.minus1YearMillis
import org.skynetsoftware.avnlauncher.utils.minus7DaysMillis
import org.skynetsoftware.avnlauncher.utils.toMidnightMillis
import java.time.ZonedDateTime

class GameDetailsViewModel(
    gameId: Int,
    gamesRepository: GamesRepository,
    settingsRepository: SettingsRepository,
    coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    val state: Flow<LoadingState> = gamesRepository.getFlow(gameId).map { game ->
        if (game == null) {
            LoadingState.Error
        } else {
            withContext(coroutineDispatchers.io) {
                val now = ZonedDateTime.now()
                val nowMillis = now.toInstant().toEpochMilli()
                val midnightToday = now.toMidnightMillis()
                val sevenDaysAgo = now.minus7DaysMillis()
                val thirtyOneDaysAgo = now.minus1MonthMillis()
                val oneYearAgo = now.minus1YearMillis()

                val playSessionsToday = ArrayList<PlaySession>()
                val playSessionsLastWeek = ArrayList<PlaySession>()
                val playSessionsLastMonth = ArrayList<PlaySession>()
                val playSessionsLastYear = ArrayList<PlaySession>()

                game.playSessions.forEach { playSession ->
                    val end = playSession.endTime
                    if (isTimeBetween(
                            midnightToday,
                            nowMillis,
                            end,
                        )
                    ) {
                        playSessionsToday.add(playSession)
                    } else if (isTimeBetween(
                            sevenDaysAgo,
                            nowMillis,
                            end,
                        )
                    ) { // last 7 days
                        playSessionsLastWeek.add(playSession)
                    } else if (isTimeBetween(
                            thirtyOneDaysAgo,
                            nowMillis,
                            end,
                        )
                    ) { // last 31 days
                        playSessionsLastMonth.add(playSession)
                    } else if (isTimeBetween(
                            oneYearAgo,
                            nowMillis,
                            end,
                        )
                    ) { // last 365 days
                        playSessionsLastYear.add(playSession)
                    }
                }

                val totalPlayTime = game.totalPlayTime
                val totalPlayTimeToday = playSessionsToday.sumOf { it.endTime - it.startTime }
                val totalPlayTimeLastWeek = playSessionsLastWeek.sumOf { it.endTime - it.startTime }
                val totalPlayTimeLastMonth = playSessionsLastMonth.sumOf { it.endTime - it.startTime }
                val totalPlayTimeLastYear = playSessionsLastYear.sumOf { it.endTime - it.startTime }

                val gameWithStats = GameWithStats(
                    game = game,
                    totalPlayTime = totalPlayTime,
                    averagePlayTimeHours = calculateAveragePlayTime(
                        firstPlayedTime = game.playSessions.minOfOrNull { it.startTime } ?: 0L,
                        lastPlayedTime = game.playSessions.maxOfOrNull { it.endTime } ?: 0L,
                        totalPlayTime = totalPlayTime,
                    ),
                    playTimeToday = totalPlayTimeToday,
                    playTime7Days = totalPlayTimeLastWeek,
                    averagePlayTimeHours7Days = calculateAveragePlayTime(
                        firstPlayedTime = playSessionsLastWeek.minOfOrNull { it.startTime } ?: 0L,
                        lastPlayedTime = playSessionsLastWeek.maxOfOrNull { it.endTime } ?: 0L,
                        totalPlayTime = totalPlayTimeLastWeek,
                    ),
                    playTime30Days = totalPlayTimeLastMonth,
                    averagePlayTimeHours30Days = calculateAveragePlayTime(
                        firstPlayedTime = playSessionsLastMonth.minOfOrNull { it.startTime } ?: 0L,
                        lastPlayedTime = playSessionsLastMonth.maxOfOrNull { it.endTime } ?: 0L,
                        totalPlayTime = totalPlayTimeLastMonth,
                    ),
                    playTime365Days = totalPlayTimeLastYear,
                    averagePlayTimeHours365Days = calculateAveragePlayTime(
                        firstPlayedTime = playSessionsLastYear.minOfOrNull { it.startTime } ?: 0L,
                        lastPlayedTime = playSessionsLastYear.maxOfOrNull { it.endTime } ?: 0L,
                        totalPlayTime = totalPlayTimeLastYear,
                    ),
                    totalPlayTimeByVersion = game.playSessions.groupBy { it.version }
                        .mapValues { it.value.sumOf { it.endTime - it.startTime } },
                )
                LoadingState.Ready(gameWithStats)
            }
        }
    }

    val showGifs = settingsRepository.showGifs

    sealed class LoadingState {
        object Loading : LoadingState()

        object Error : LoadingState()

        class Ready(val game: GameWithStats) : LoadingState()
    }
}
