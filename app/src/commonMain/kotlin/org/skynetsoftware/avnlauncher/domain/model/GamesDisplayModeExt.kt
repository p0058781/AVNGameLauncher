package org.skynetsoftware.avnlauncher.domain.model

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.grid
import org.skynetsoftware.avnlauncher.app.generated.resources.list

@OptIn(ExperimentalResourceApi::class)
fun GamesDisplayMode.iconRes(): DrawableResource {
    return when (this) {
        GamesDisplayMode.Grid -> Res.drawable.grid
        GamesDisplayMode.List -> Res.drawable.list
    }
}
