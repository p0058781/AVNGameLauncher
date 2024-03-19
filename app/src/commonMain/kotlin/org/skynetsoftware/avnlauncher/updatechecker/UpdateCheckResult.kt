package org.skynetsoftware.avnlauncher.updatechecker

import org.skynetsoftware.avnlauncher.domain.model.Game

data class UpdateCheckResult(
    val updates: List<Game>,
    val exceptions: List<Throwable>,
)
