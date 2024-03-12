package org.skynetsoftware.avnlauncher.ui.screen.editgame

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.ui.screen.Dialog
import org.skynetsoftware.avnlauncher.ui.screen.GamePicker
import org.skynetsoftware.avnlauncher.ui.viewmodel.EditGameViewModel
import org.skynetsoftware.avnlauncher.ui.viewmodel.MainViewModel
import org.skynetsoftware.avnlauncher.utils.format

@OptIn(ExperimentalResourceApi::class)
@Composable
fun EditGameDialog(
    editGameViewModel: EditGameViewModel,
    mainViewModel: MainViewModel = koinInject(),
    onCloseRequest: () -> Unit = {},
) {
    var title by remember { editGameViewModel.title }.collectAsMutableState()
    var imageUrl by remember { editGameViewModel.imageUrl }.collectAsMutableState()
    val executablePaths by remember { editGameViewModel.executablePaths }.collectAsState()
    var checkForUpdates by remember { editGameViewModel.checkForUpdates }.collectAsMutableState()

    Dialog(
        title = R.strings.editGameDialogTitle.format(title),
        onDismiss = onCloseRequest,
    ) {
        Surface(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            var showFilePicker by remember { mutableStateOf(-1) }
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // TODO label should be platform appropriate
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
                executablePaths.forEachIndexed { index, executablePath ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        TextField(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            value = executablePath,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(R.strings.editGameInputLabelExecutablePath) },
                            trailingIcon = {
                                Image(
                                    painter = painterResource(R.images.edit),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp).clickable {
                                        showFilePicker = index
                                    },
                                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                                )
                            },
                        )
                        Spacer(
                            modifier = Modifier.width(10.dp),
                        )
                        if (index == executablePaths.size - 1) { // last one
                            Image(
                                modifier = Modifier.align(Alignment.CenterVertically).size(24.dp).clickable {
                                    editGameViewModel.addExecutablePath()
                                },
                                painter = painterResource(R.images.add),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                            )
                        } else {
                            Image(
                                modifier = Modifier.align(Alignment.CenterVertically).size(24.dp).clickable {
                                    editGameViewModel.deleteExecutablePath(index)
                                },
                                painter = painterResource(R.images.close),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
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
            if (showFilePicker > 0) {
                var currentPath = executablePaths[showFilePicker]
                if (currentPath.isEmpty()) {
                    currentPath = executablePaths.find { it.isNotEmpty() } ?: currentPath
                }
                GamePicker(showFilePicker > 0, currentPath) {
                    it?.let {
                        editGameViewModel.setExecutablePath(showFilePicker, it)
                    }
                    showFilePicker = -1
                }
            }
        }
    }
}
