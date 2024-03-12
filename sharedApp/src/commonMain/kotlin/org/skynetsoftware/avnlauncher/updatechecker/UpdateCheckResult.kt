package org.skynetsoftware.avnlauncher.updatechecker

import org.skynetsoftware.avnlauncher.domain.model.Game

data class UpdateCheckResult(
    val games: List<UpdateCheckGame>,
)

data class UpdateCheckGame(
    val game: Game,
    val updateAvailable: Boolean,
    val exception: Throwable?,
)
