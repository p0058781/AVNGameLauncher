package org.skynetsoftware.avnlauncher.domain.model

data class GameWithStats(
    val game: Game,
    val totalPlayTime: Long,
    val averagePlayTimeHours: Float,
    val playTimeToday: Long,
    val playTime7Days: Long,
    val averagePlayTimeHours7Days: Float,
    val playTime30Days: Long,
    val averagePlayTimeHours30Days: Float,
    val playTime365Days: Long,
    val averagePlayTimeHours365Days: Float,
    val totalPlayTimeByVersion: Map<String?, Long>,
)
