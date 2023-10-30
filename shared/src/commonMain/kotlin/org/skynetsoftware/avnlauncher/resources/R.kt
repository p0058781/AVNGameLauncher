@file:Suppress("ClassName", "ConstPropertyName")

package org.skynetsoftware.avnlauncher.resources

//TODO This is a dumb temporary "solution" since moko-resources doesn't support kotlin 1.9 and compose 1.5 yet
object R {
    object strings {
        const val editGameDialogTitle = "Edit '%s'"
        const val toastException = "Exceptions: "
        const val toastNoUpdatesAvailable = "No Updates Available"
        const val toastUpdateAvailable = "Update Available:"
        const val filterTitle = "Filter (%d)"
        const val sortOrderTitle = "Sort Order"
        const val infoLabelPlayTime = "Play Time:"
        const val infoLabelVersion = "Version:"
        const val infoLabelAvailableVersion = "Available Version:"
        const val infoLabelReleaseDate = "Release Date:"
        const val noValue = "-"
        const val editGameInputLabelTitle = "Title"
        const val editGameInputLabelImageUrl = "Image URL"
        const val editGameInputLabelExecutablePath = "Executable Path"
        const val editGameToastGameUpdated = "Game Updated"
        const val editGameButtonSave = "Save"
    }

    object images {
        const val playing = "playing.png"
        const val refresh = "refresh.png"
        const val import = "import.png"
        const val completed = "completed.png"
        const val waiting = "waiting.png"
        const val update = "update.png"
        const val warning = "warning.png"
        const val visible = "visible.png"
        const val gone = "gone.png"
        const val edit = "edit.png"
    }

}