package org.skynetsoftware.avnlauncher.ui.screen.import

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.data.GameImport
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.ui.screen.Dialog
import org.skynetsoftware.avnlauncher.ui.screen.ImportGamesPicker
import org.skynetsoftware.avnlauncher.ui.theme.Foreground
import org.skynetsoftware.avnlauncher.ui.theme.Gray
import org.skynetsoftware.avnlauncher.utils.format

//TODO use viewmodel
@OptIn(ExperimentalResourceApi::class)
@Composable
fun ImportGamesDialog(
    gameImport: GameImport = koinInject(),
    onCloseRequest: () -> Unit = {},
    showToast: (message: String) -> Unit = {},
) {
    var executablePath by remember { mutableStateOf<String?>(null) }
    var importing by remember { mutableStateOf(false) }
    var showFilePicker by remember { mutableStateOf(false) }

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
                        value = executablePath ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(R.strings.editGameInputLabelExecutablePath) },
                        trailingIcon = {
                            Image(
                                painter = painterResource(R.images.edit),
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
                        enabled = executablePath.isNullOrBlank().not(),
                        onClick = {
                            executablePath?.let {
                                importing = true
                                gameImport.importGames(it) { imported ->
                                    importing = false
                                    onCloseRequest()
                                    showToast(R.strings.importGamesDialogToast.format(imported))
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
                ImportGamesPicker(showFilePicker) {
                    showFilePicker = false
                    it.let {
                        executablePath = it
                    }
                }
            }

        }
    }
}