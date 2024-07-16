package org.skynetsoftware.avnlauncher.data.mapper

import org.skynetsoftware.avnlauncher.data.PlayStateEntity
import org.skynetsoftware.avnlauncher.domain.model.PlayState

internal fun PlayState.toPlayStateEntity() =
    PlayStateEntity(
        id = id,
        label = label,
        description = description,
    )

internal fun PlayStateEntity.toPlayState() =
    PlayState(
        id = id,
        label = label,
        description = description,
    )
