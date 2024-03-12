package org.skynetsoftware.avnlauncher.ui.screen.editgame

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import org.skynetsoftware.avnlauncher.ui.viewmodel.EditGameViewModel
import org.skynetsoftware.avnlauncher.ui.viewmodel.MainViewModel
import org.skynetsoftware.avnlauncher.utils.format

@OptIn(ExperimentalResourceApi::class)
@Composable
fun EditGameDialog(
    _selectedGame: Game,
    editGameViewModel: EditGameViewModel = koinInject(parameters = { parametersOf(_selectedGame) }),
    mainViewModel: MainViewModel = koinInject(),
    onCloseRequest: () -> Unit = {},
) {
    var title by remember { editGameViewModel.title }.collectAsMutableState()
    var imageUrl by remember { editGameViewModel.imageUrl }.collectAsMutableState()
    var executablePath by remember { editGameViewModel.executablePath }.collectAsMutableState()
    var checkForUpdates by remember { editGameViewModel.checkForUpdates }.collectAsMutableState()

    Dialog(
        title = R.strings.editGameDialogTitle.format(title),
        onDismiss = onCloseRequest,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            var showFilePicker by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    label = { Text(R.strings.editGameInputLabelTitle) },
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = imageUrl,
                    onValueChange = {
                        imageUrl = it
                    },
                    label = { Text(R.strings.editGameInputLabelImageUrl) },
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
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                        )
                    },
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Checkbox(
                        checked = checkForUpdates,
                        onCheckedChange = { checked ->
                            checkForUpdates = checked
                        },
                    )
                    Text(
                        text = R.strings.editGameDialogCheckForUpdates,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        editGameViewModel.save()
                        onCloseRequest()
                        mainViewModel.showToast(R.strings.editGameToastGameUpdated)
                    },
                    shape = MaterialTheme.shapes.medium,
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
