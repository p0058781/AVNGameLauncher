package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.runtime.Composable
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import org.skynetsoftware.avnlauncher.utils.OS
import org.skynetsoftware.avnlauncher.utils.os

@Composable
actual fun GamePicker(
    show: Boolean,
    currentExecutable: String?,
    onGamePicked: (game: String?) -> Unit,
) {
    when (os) {
        OS.Linux -> FilePicker(show, currentExecutable, fileExtensions = listOf("sh")) { path ->
            onGamePicked(path?.path)
        }

        OS.Windows -> FilePicker(show, currentExecutable, fileExtensions = listOf("exe")) { path ->
            onGamePicked(path?.path)
        }

        OS.Mac -> FilePicker(show, currentExecutable, fileExtensions = listOf("app")) { path ->
            onGamePicked(path?.path)
        }
    }
}

@Composable
actual fun GamesDirPicker(
    visible: Boolean,
    currentDir: String?,
    onDirPicked: (dir: String?) -> Unit,
) {
    DirectoryPicker(visible, currentDir) { path ->
        onDirPicked(path)
    }
}
