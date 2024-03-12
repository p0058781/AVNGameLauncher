package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.runtime.Composable
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import org.skynetsoftware.avnlauncher.utils.OS
import org.skynetsoftware.avnlauncher.utils.os

// TODO should specify initial file/dir

@Composable
actual fun GamePicker(
    visible: Boolean,
    onGamePicked: (game: String?) -> Unit,
) {
    when (os) {
        OS.Linux -> FilePicker(visible, fileExtensions = listOf("sh")) { path ->
            onGamePicked(path?.path)
        }

        OS.Windows -> FilePicker(visible, fileExtensions = listOf("exe")) { path ->
            onGamePicked(path?.path)
        }

        OS.Mac -> FilePicker(visible, fileExtensions = listOf("app")) { path ->
            onGamePicked(path?.path)
        }
    }
}

@Composable
actual fun GamesDirPicker(
    visible: Boolean,
    onDirPicked: (dir: String?) -> Unit,
) {
    DirectoryPicker(visible) { path ->
        onDirPicked(path)
    }
}
