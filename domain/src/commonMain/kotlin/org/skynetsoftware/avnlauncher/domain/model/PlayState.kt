package org.skynetsoftware.avnlauncher.domain.model

data class PlayState(
    val id: String,
    val label: String,
    val description: String?,
)

val PLAY_STATE_NONE = PlayState(
    id = "None",
    label = "-",
    description = null,
)
