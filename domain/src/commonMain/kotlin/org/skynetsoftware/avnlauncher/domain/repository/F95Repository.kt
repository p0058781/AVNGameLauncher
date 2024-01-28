package org.skynetsoftware.avnlauncher.domain.repository

import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.utils.Result

interface F95Repository {
    suspend fun getGame(gameThreadId: Int): Result<Game>

    suspend fun getRedirectUrl(gameThreadId: Int): Result<String>
}
