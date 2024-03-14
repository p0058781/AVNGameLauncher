package org.skynetsoftware.avnlauncher.domain.model

import org.skynetsoftware.avnlauncher.resources.Images

fun GamesDisplayMode.iconRes(): String {
    return when (this) {
        GamesDisplayMode.Grid -> Images.grid
        GamesDisplayMode.List -> Images.list
    }
}
