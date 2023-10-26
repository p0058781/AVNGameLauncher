package org.skynetsoftware.avnlauncher.data.model

enum class Filter(val label: String) {
    All("All"),
    GamesWithUpdate("Games With Update"),
    HiddenGames("Hidden Games"),
    UnplayedGames("Unplayed Games"),
    Playing("Playing"),
    Completed("Completed"),
    WaitingForUpdate("Waiting For Update"),
}