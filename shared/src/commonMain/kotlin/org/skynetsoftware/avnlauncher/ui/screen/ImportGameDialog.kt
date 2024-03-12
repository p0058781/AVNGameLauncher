package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.data.GameImport
import org.skynetsoftware.avnlauncher.ui.theme.Foreground
import org.skynetsoftware.avnlauncher.ui.theme.Gray

@Composable
fun ImportGameDialog(
    gameImport: GameImport = koinInject(),
    onCloseRequest: () -> Unit = {},
    showToast: (message: String) -> Unit = {},
) {
    var threadId by remember { mutableStateOf<String?>(null) }
    var importing by remember { mutableStateOf(false) }

    DialogWindow(
        title = "Import Game",
        onCloseRequest = onCloseRequest,
        resizable = false,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (importing) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = threadId ?: "",
                        onValueChange = {
                            threadId = it
                        },
                        label = { Text("F95 Thread ID") }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = threadId.isNullOrBlank().not(),
                        onClick = {
                            threadId?.let {
                                importing = true
                                gameImport.importGame(it.toInt()) {
                                    importing = false
                                    onCloseRequest()
                                    showToast("Game Imported: '$it'")
                                }
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Gray, contentColor = Foreground),
                    ) {
                        Text(
                            text = "Import",
                        )
                    }
                }
            }

        }
    }
}