package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.runtime.Composable
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import org.skynetsoftware.avnlauncher.utils.OS
import org.skynetsoftware.avnlauncher.utils.os


@Composable
actual fun GamePicker(visible: Boolean, onGamePicked: (game: String?) -> Unit) {
    when (os) {
        OS.Linux -> ExecutableFilePicker(visible, "sh") { path ->
            onGamePicked(path)
        }

        OS.Windows -> ExecutableFilePicker(visible, "exe") { path ->
            onGamePicked(path)
        }

        OS.Mac -> MacAppDirectoryPicker(visible) { path ->
            onGamePicked(path)
        }
    }
}


@Composable
private fun ExecutableFilePicker(showFilePicker: Boolean, extension: String, onFilePicked: (path: String?) -> Unit) {
    FilePicker(showFilePicker, fileExtensions = listOf(extension)) { path ->
        onFilePicked(path?.path)
    }
}

@Composable
private fun MacAppDirectoryPicker(showFilePicker: Boolean, onFilePicked: (path: String?) -> Unit) {
    DirectoryPicker(showFilePicker) { path ->
        onFilePicked(path)
    }
}