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
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.ui.screen.Dialog
import org.skynetsoftware.avnlauncher.ui.screen.GamePicker
import org.skynetsoftware.avnlauncher.ui.viewmodel.EditGameScreenModel
import org.skynetsoftware.avnlauncher.ui.viewmodel.MainScreenModel
import org.skynetsoftware.avnlauncher.utils.collectAsMutableState

@Suppress("LongMethod")
@OptIn(ExperimentalResourceApi::class)
@Composable
fun EditGameDialog(
    editGameScreenModel: EditGameScreenModel,
    mainScreenModel: MainScreenModel = koinInject(),
    onCloseRequest: () -> Unit = {},
) {
    var title by remember { editGameScreenModel.title }.collectAsMutableState()
    var imageUrl by remember { editGameScreenModel.imageUrl }.collectAsMutableState()
    val executablePaths by remember { editGameScreenModel.executablePaths }.collectAsState()
    var checkForUpdates by remember { editGameScreenModel.checkForUpdates }.collectAsMutableState()

    Dialog(
        title = stringResource(MR.strings.editGameDialogTitle, title),
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
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    label = { Text(stringResource(MR.strings.editGameInputLabelTitle)) },
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = imageUrl,
                    onValueChange = {
                        imageUrl = it
                    },
                    label = { Text(stringResource(MR.strings.editGameInputLabelImageUrl)) },
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
                            label = { Text(stringResource(MR.strings.editGameInputLabelExecutablePath)) },
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
                                    editGameScreenModel.addExecutablePath()
                                },
                                painter = painterResource(R.images.add),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                            )
                        } else {
                            Image(
                                modifier = Modifier.align(Alignment.CenterVertically).size(24.dp).clickable {
                                    editGameScreenModel.deleteExecutablePath(index)
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
                        text = stringResource(MR.strings.editGameDialogCheckForUpdates),
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        editGameScreenModel.save()
                        onCloseRequest()
                        mainScreenModel.showToast(MR.strings.editGameToastGameUpdated)
                    },
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(
                        text = stringResource(MR.strings.editGameButtonSave),
                    )
                }
            }
            if (showFilePicker >= 0) {
                var currentPath = executablePaths[showFilePicker]
                if (currentPath.isEmpty()) {
                    currentPath = executablePaths.find { it.isNotEmpty() } ?: currentPath
                }
                GamePicker(showFilePicker >= 0, currentPath) {
                    it?.let {
                        editGameScreenModel.setExecutablePath(showFilePicker, it)
                    }
                    showFilePicker = -1
                }
            }
        }
    }
}
