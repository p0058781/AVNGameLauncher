package org.skynetsoftware.avnlauncher.ui.screen.editgame

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
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.ui.screen.Dialog
import org.skynetsoftware.avnlauncher.ui.screen.GamePicker
import org.skynetsoftware.avnlauncher.ui.theme.Foreground
import org.skynetsoftware.avnlauncher.ui.theme.Gray
import org.skynetsoftware.avnlauncher.utils.format

@OptIn(ExperimentalResourceApi::class)
@Composable
fun EditGameDialog(
    _selectedGame: Game,
    editGameViewModel: EditGameViewModel = koinInject(parameters = { parametersOf(_selectedGame) }),
    onCloseRequest: () -> Unit = {},
    showToast: (message: String) -> Unit = {},
) {

    var title by remember { editGameViewModel.title }.collectAsMutableState()
    var imageUrl by remember { editGameViewModel.imageUrl }.collectAsMutableState()
    var executablePath by remember { editGameViewModel.executablePath }.collectAsMutableState()

    Dialog(
        title = R.strings.editGameDialogTitle.format(title),
        onDismiss = onCloseRequest,
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
                    label = { Text(R.strings.editGameInputLabelTitle) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = imageUrl,
                    onValueChange = {
                        imageUrl = it
                    },
                    label = { Text(R.strings.editGameInputLabelImageUrl) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = executablePath,
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
                    onClick = {
                        editGameViewModel.save()
                        onCloseRequest()
                        showToast(R.strings.editGameToastGameUpdated)
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Gray, contentColor = Foreground),
                ) {
                    Text(
                        text = R.strings.editGameButtonSave,
                    )
                }
            }
            GamePicker(showFilePicker) {
                showFilePicker = false
                it?.let {
                    executablePath = it
                }
            }

        }
    }
}

