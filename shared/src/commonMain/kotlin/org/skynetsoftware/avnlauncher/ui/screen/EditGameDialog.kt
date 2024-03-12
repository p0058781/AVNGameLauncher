package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.ui.theme.Foreground
import org.skynetsoftware.avnlauncher.ui.theme.Gray
import org.skynetsoftware.avnlauncher.utils.OS
import org.skynetsoftware.avnlauncher.utils.os

@Composable
fun EditGameDialog(
    selectedGame: Game,
    gamesRepository: GamesRepository = koinInject(),
    onCloseRequest: () -> Unit = {},
    showToast: (message: String) -> Unit = {},
) {

    var title by remember { mutableStateOf(selectedGame.title) }
    var imageUrl by remember { mutableStateOf(selectedGame.imageUrl) }
    var executablePath by remember { mutableStateOf(selectedGame.executablePath) }

    DialogWindow(
        title = "Edit '${selectedGame.title}'",
        onCloseRequest = onCloseRequest,
        resizable = false,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            var showFilePicker by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    label = { Text("Title") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = imageUrl,
                    onValueChange = {
                        imageUrl = it
                    },
                    label = { Text("Image URL") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = executablePath,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Executable Path") },
                    trailingIcon = {
                        Image(
                            painter = painterResource("edit.png"),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp).clickable {
                                showFilePicker = true
                            },
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if(executablePath != selectedGame.executablePath) {
                            gamesRepository.updateExecutablePath(executablePath, selectedGame.id)
                        }
                        if(title != selectedGame.title) {
                            gamesRepository.updateTitle(title, selectedGame.id)
                        }
                        if(imageUrl != selectedGame.imageUrl) {
                            gamesRepository.updateImageUrl(imageUrl, selectedGame.id)
                        }
                        onCloseRequest()
                        showToast("Game Updated")
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Gray, contentColor = Foreground),
                ) {
                    Text(
                        text = "Save",
                    )
                }
            }
            when(os) {
                OS.Linux -> ExecutableFilePicker(showFilePicker, "sh") { path ->
                    showFilePicker = false
                    path?.let {
                        executablePath = path
                    }
                }
                OS.Windows -> ExecutableFilePicker(showFilePicker, "exe") { path ->
                    showFilePicker = false
                    path?.let {
                        executablePath = path
                    }
                }
                OS.Mac -> MacAppDirectoryPicker(showFilePicker) { path ->
                    showFilePicker = false
                    path?.let {
                        executablePath = path
                    }
                }
            }

        }
    }
}

@Composable
fun ExecutableFilePicker(showFilePicker: Boolean, extension: String, onFilePicked: (path: String?) -> Unit) {
    FilePicker(showFilePicker, fileExtensions = listOf(extension)) { path ->
        onFilePicked(path?.path)
    }
}

@Composable
fun MacAppDirectoryPicker(showFilePicker: Boolean, onFilePicked: (path: String?) -> Unit) {
    DirectoryPicker(showFilePicker) { path ->
        onFilePicked(path)
    }
}