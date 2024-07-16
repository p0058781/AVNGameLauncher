package org.skynetsoftware.avnlauncher.ui.screen.customlists

import org.jetbrains.compose.resources.StringResource

data class GamesListViewItem(
    val id: Int,
    val name: String,
    val description: String?,
    val editing: Boolean,
    val error: StringResource?,
)
