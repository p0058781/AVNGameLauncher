package org.skynetsoftware.avnlauncher.data.model

enum class PlayState {
    None,
    Playing,
    WaitingForUpdate,
    Completed,
    ;

    companion object {
        fun fromString(playState: String?): PlayState {
            return entries.find { it.name == playState } ?: None
        }
    }
}
