package org.skynetsoftware.avnlauncher.domain.repository

import kotlinx.coroutines.flow.Flow
import org.skynetsoftware.avnlauncher.domain.model.PlayState

interface PlayStateRepository {
    val playStates: Flow<List<PlayState>>

    fun getById(id: String): PlayState?

    fun getByLabel(label: String): PlayState?

    fun insert(playState: PlayState)

    fun update(
        id: String,
        label: String,
        description: String?,
    )

    fun delete(id: String)
}
