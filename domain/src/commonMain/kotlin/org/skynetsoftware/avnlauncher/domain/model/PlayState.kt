package org.skynetsoftware.avnlauncher.domain.model

enum class PlayState {
    Playing,
    WaitingForUpdate,
    Completed,
    ;

    companion object {
        fun fromString(playState: String?): PlayState {
            return entries.find { it.name == playState } ?: error("Invalid value '$playState'")
        }
    }
}
