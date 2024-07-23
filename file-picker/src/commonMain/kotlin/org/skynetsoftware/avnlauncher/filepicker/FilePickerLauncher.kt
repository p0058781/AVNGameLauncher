/**
 * Simple File Picker for compose-multiplatform, targeting JVM only
 * Inspired by: https://github.com/vinceglb/FileKit
 */
package org.skynetsoftware.avnlauncher.filepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.launch
import java.io.File

class FilePickerLauncher(
    val onLaunch: () -> Unit,
) {
    fun launch() {
        onLaunch()
    }
}

@Composable
fun rememberFilePickerLauncher(
    pickerType: PickerType,
    initialDirectory: String? = null,
    title: String? = null,
    extensions: Array<String>? = null,
    onFilePicked: (file: File?) -> Unit,
): FilePickerLauncher {
    val coroutineScope = rememberCoroutineScope()

    val currentPickerType by rememberUpdatedState(pickerType)
    val currentInitialDirectory by rememberUpdatedState(initialDirectory)
    val currentTitle by rememberUpdatedState(title)
    val currentExtensions by rememberUpdatedState(extensions)
    val currentOnFilePicked by rememberUpdatedState(onFilePicked)

    return remember {
        FilePickerLauncher {
            coroutineScope.launch {
                currentOnFilePicked(
                    FilePicker.pickFile(
                        pickerType = currentPickerType,
                        initialDirectory = currentInitialDirectory,
                        title = currentTitle,
                        extensions = currentExtensions,
                    ),
                )
            }
        }
    }
}
