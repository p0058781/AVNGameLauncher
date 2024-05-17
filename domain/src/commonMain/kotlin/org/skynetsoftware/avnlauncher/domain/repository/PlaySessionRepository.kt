package org.skynetsoftware.avnlauncher.domain.repository

import org.skynetsoftware.avnlauncher.domain.model.PlaySession

interface PlaySessionRepository {
    suspend fun insertPlaySession(playSession: PlaySession)
}
