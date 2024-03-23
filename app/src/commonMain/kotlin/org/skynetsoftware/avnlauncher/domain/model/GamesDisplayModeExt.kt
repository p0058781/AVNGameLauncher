package org.skynetsoftware.avnlauncher.domain.model

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.grid_view
import org.skynetsoftware.avnlauncher.app.generated.resources.view_list

@OptIn(ExperimentalResourceApi::class)
fun GamesDisplayMode.iconRes(): DrawableResource {
    return when (this) {
        GamesDisplayMode.Grid -> Res.drawable.grid_view
        GamesDisplayMode.List -> Res.drawable.view_list
    }
}
