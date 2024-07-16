package org.skynetsoftware.avnlauncher.data.mapper

import org.skynetsoftware.avnlauncher.data.ListEntity
import org.skynetsoftware.avnlauncher.domain.model.GamesList

internal fun GamesList.toListEntity() =
    ListEntity(
        id = id,
        name = name,
        description = description,
    )

internal fun ListEntity.toGamesList() =
    GamesList(
        id = id,
        name = name,
        description = description,
    )
