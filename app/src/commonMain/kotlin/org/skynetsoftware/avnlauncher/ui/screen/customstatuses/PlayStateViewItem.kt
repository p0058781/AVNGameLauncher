package org.skynetsoftware.avnlauncher.ui.screen.customstatuses

import org.jetbrains.compose.resources.StringResource

data class PlayStateViewItem(
    val id: String,
    val label: String,
    val description: String?,
    val editing: Boolean,
    val error: StringResource?,
)
