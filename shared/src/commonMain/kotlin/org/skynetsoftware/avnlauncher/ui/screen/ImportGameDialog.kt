package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.data.GameImport
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.ui.theme.Foreground
import org.skynetsoftware.avnlauncher.ui.theme.Gray
import org.skynetsoftware.avnlauncher.utils.format

@Composable
fun ImportGameDialog(
    gameImport: GameImport = koinInject(),
    onCloseRequest: () -> Unit = {},
    showToast: (message: String) -> Unit = {},
) {
    var threadId by remember { mutableStateOf<String?>(null) }
    var importing by remember { mutableStateOf(false) }

    Dialog(
        title = R.strings.importGameDialogTitle,
        onDismiss = onCloseRequest,
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
                        label = { Text(R.strings.importGameDialogThreadIdHint) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = threadId.isNullOrBlank().not(),
                        onClick = {
                            threadId?.let {
                                importing = true
                                gameImport.importGame(it.toInt()) { title ->
                                    importing = false
                                    onCloseRequest()
                                    showToast(R.strings.importGameDialogSuccessToast.format(title))
                                }
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Gray, contentColor = Foreground),
                    ) {
                        Text(
                            text = R.strings.importGameDialogButtonImport,
                        )
                    }
                }
            }

        }
    }
}