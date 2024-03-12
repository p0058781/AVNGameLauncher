package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.runtime.Composable
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import org.skynetsoftware.avnlauncher.utils.OS
import org.skynetsoftware.avnlauncher.utils.os


@Composable
actual fun GamePicker(visible: Boolean, onGamePicked: (game: String?) -> Unit) {
    when (os) {
        OS.Linux -> FilePicker(visible, fileExtensions = listOf("sh")) { path ->
            onGamePicked(path?.path)
        }

        OS.Windows -> FilePicker(visible, fileExtensions = listOf("exe")) { path ->
            onGamePicked(path?.path)
        }

        OS.Mac -> DirectoryPicker(visible) { path ->
            onGamePicked(path)
        }
    }
}

@Composable
actual fun ImportGamesPicker(visible: Boolean, onFilePicked: (file: String?) -> Unit) {
    FilePicker(visible, fileExtensions = listOf("json")) { path ->
        onFilePicked(path?.path)
    }
}