package org.skynetsoftware.avnlauncher.ui.screen.editgame

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
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
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.domain.model.PlayState
import org.skynetsoftware.avnlauncher.domain.model.label
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.ui.screen.Dialog
import org.skynetsoftware.avnlauncher.ui.screen.GamePicker
import org.skynetsoftware.avnlauncher.ui.screen.applyPlatformSpecificHeight
import org.skynetsoftware.avnlauncher.ui.viewmodel.EditGameScreenModel
import org.skynetsoftware.avnlauncher.ui.viewmodel.MainScreenModel
import org.skynetsoftware.avnlauncher.utils.collectAsMutableState

@Suppress("LongMethod")
@Composable
fun EditGameDialog(
    editGameScreenModel: EditGameScreenModel,
    mainScreenModel: MainScreenModel = koinInject(),
    onCloseRequest: () -> Unit = {},
) {
    var title by remember { editGameScreenModel.title }.collectAsMutableState(context = Dispatchers.Main.immediate)
    var imageUrl by remember { editGameScreenModel.imageUrl }
        .collectAsMutableState(context = Dispatchers.Main.immediate)
    var notes by remember { editGameScreenModel.notes }
        .collectAsMutableState(context = Dispatchers.Main.immediate)
    val executablePaths by remember { editGameScreenModel.executablePaths }.collectAsState()
    var checkForUpdates by remember { editGameScreenModel.checkForUpdates }.collectAsMutableState()
    var currentPlayState by remember { editGameScreenModel.currentPlayState }.collectAsMutableState()
    var hidden by remember { editGameScreenModel.hidden }.collectAsMutableState()

    Dialog(
        title = stringResource(MR.strings.editGameDialogTitle, title),
        onDismiss = onCloseRequest,
    ) {
        Surface(
            modifier = Modifier.applyPlatformSpecificHeight().verticalScroll(rememberScrollState()),
        ) {
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
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = notes ?: "",
                    onValueChange = {
                        notes = it
                    },
                    label = { Text(stringResource(MR.strings.editGameInputLabelNotes)) },
                )
                Spacer(modifier = Modifier.height(10.dp))
                ExecutablePaths(
                    executablePaths = executablePaths,
                    addExecutablePath = editGameScreenModel::addExecutablePath,
                    deleteExecutablePath = editGameScreenModel::deleteExecutablePath,
                    setExecutablePath = editGameScreenModel::setExecutablePath,
                )
                PlayStates(
                    currentPlayState = currentPlayState,
                    setPlayState = {
                        currentPlayState = it
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
                        text = stringResource(MR.strings.editGameDialogCheckForUpdates),
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Checkbox(
                        checked = hidden,
                        onCheckedChange = { checked ->
                            hidden = checked
                        },
                    )
                    Text(
                        text = stringResource(MR.strings.editGameDialogArchived),
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
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
fun PlayStates(
    currentPlayState: PlayState,
    setPlayState: (playState: PlayState) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
    ) {
        PlayState.entries.forEach { playState ->
            FilterChip(
                modifier = Modifier.padding(horizontal = 5.dp),
                selected = currentPlayState == playState,
                onClick = {
                    setPlayState(playState)
                },
                shape = MaterialTheme.shapes.small,
            ) {
                Text(
                    text = playState.label(),
                )
            }
        }
    }
}

@Suppress("LongMethod")
@OptIn(ExperimentalResourceApi::class)
@Composable
fun ExecutablePaths(
    executablePaths: List<String>,
    addExecutablePath: () -> Unit,
    deleteExecutablePath: (index: Int) -> Unit,
    setExecutablePath: (index: Int, executablePath: String) -> Unit,
) {
    var showFilePicker by remember { mutableStateOf(-1) }
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
                        addExecutablePath()
                    },
                    painter = painterResource(R.images.add),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                )
            } else {
                Image(
                    modifier = Modifier.align(Alignment.CenterVertically).size(24.dp).clickable {
                        deleteExecutablePath(index)
                    },
                    painter = painterResource(R.images.close),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
    if (showFilePicker >= 0) {
        var currentPath = executablePaths[showFilePicker]
        if (currentPath.isEmpty()) {
            currentPath = executablePaths.find { it.isNotEmpty() } ?: currentPath
        }
        GamePicker(showFilePicker >= 0, currentPath) {
            it?.let {
                setExecutablePath(showFilePicker, it)
            }
            showFilePicker = -1
        }
    }
}
