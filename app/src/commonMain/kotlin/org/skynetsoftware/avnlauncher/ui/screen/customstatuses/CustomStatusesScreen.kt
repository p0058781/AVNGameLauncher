package org.skynetsoftware.avnlauncher.ui.screen.customstatuses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.customPlayStatesAddNew
import org.skynetsoftware.avnlauncher.app.generated.resources.customPlayStatesDeleteCancelButton
import org.skynetsoftware.avnlauncher.app.generated.resources.customPlayStatesDeleteConfirmButton
import org.skynetsoftware.avnlauncher.app.generated.resources.customPlayStatesDeleteText
import org.skynetsoftware.avnlauncher.app.generated.resources.customPlayStatesDeleteTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.customPlayStatesInputHintDescription
import org.skynetsoftware.avnlauncher.app.generated.resources.customPlayStatesInputHintLabel
import org.skynetsoftware.avnlauncher.app.generated.resources.customPlayStatesInputLabelDescription
import org.skynetsoftware.avnlauncher.app.generated.resources.customPlayStatesInputLabelLabel
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModel

@Composable
fun CustomStatusesScreen(customStatusesViewModel: CustomStatusesViewModel = viewModel()) {
    val playStates by remember { customStatusesViewModel.playStates }.collectAsState(emptyList())
    var confirmDeletePlayState by remember { mutableStateOf<PlayStateViewItem?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column {
            TextButton(
                onClick = {
                    customStatusesViewModel.add()
                },
            ) {
                Text(
                    text = stringResource(Res.string.customPlayStatesAddNew),
                )
            }
            Divider()
            LazyColumn {
                items(playStates) { playState ->
                    PlayStateItem(
                        playState = playState,
                        edit = {
                            customStatusesViewModel.edit(it)
                        },
                        delete = {
                            confirmDeletePlayState = it
                        },
                        save = { playStateItem, label, description ->
                            customStatusesViewModel.save(playStateItem, label, description)
                        },
                    )
                    Divider()
                }
            }
        }
    }

    confirmDeletePlayState?.let {
        AlertDialog(
            onDismissRequest = {
                confirmDeletePlayState = null
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmDeletePlayState = null
                        customStatusesViewModel.delete(it)
                    },
                ) {
                    Text(
                        text = stringResource(Res.string.customPlayStatesDeleteConfirmButton),
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        confirmDeletePlayState = null
                    },
                ) {
                    Text(
                        text = stringResource(Res.string.customPlayStatesDeleteCancelButton),
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(Res.string.customPlayStatesDeleteTitle, it.label),
                )
            },
            text = {
                Text(
                    text = stringResource(Res.string.customPlayStatesDeleteText),
                )
            },
        )
    }
}

@Composable
private fun PlayStateItem(
    playState: PlayStateViewItem,
    edit: (playState: PlayStateViewItem) -> Unit,
    delete: (playState: PlayStateViewItem) -> Unit,
    save: (playState: PlayStateViewItem, label: String, description: String?) -> Unit,
) {
    Box(
        modifier = Modifier.padding(10.dp).fillMaxWidth(),
    ) {
        if (playState.editing) {
            var label by remember { mutableStateOf(playState.label) }
            var description by remember { mutableStateOf(playState.description) }
            Row {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp).fillMaxWidth(),
                        value = label,
                        onValueChange = {
                            label = it
                        },
                        label = {
                            Text(
                                text = stringResource(Res.string.customPlayStatesInputLabelLabel),
                            )
                        },
                        placeholder = {
                            Text(
                                text = stringResource(Res.string.customPlayStatesInputHintLabel),
                            )
                        },
                        isError = playState.error != null,
                    )
                    playState.error?.let {
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            text = stringResource(it),
                            color = MaterialTheme.colors.error,
                        )
                    }
                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp).fillMaxWidth(),
                        value = description ?: "",
                        onValueChange = {
                            description = it
                        },
                        label = {
                            Text(
                                text = stringResource(Res.string.customPlayStatesInputLabelDescription),
                            )
                        },
                        placeholder = {
                            Text(
                                text = stringResource(Res.string.customPlayStatesInputHintDescription),
                            )
                        },
                    )
                }
                Spacer(
                    modifier = Modifier.width(10.dp),
                )
                Icon(
                    modifier = Modifier.align(Alignment.CenterVertically).clickable {
                        save(playState, label, description)
                    },
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                )
            }
        } else {
            Row {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = playState.label,
                        color = MaterialTheme.colors.onPrimary,
                    )
                    playState.description?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.body2,
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(10.dp),
                )
                Icon(
                    modifier = Modifier.align(Alignment.CenterVertically).clickable {
                        edit(playState)
                    },
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                )
                Icon(
                    modifier = Modifier.align(Alignment.CenterVertically).clickable {
                        delete(playState)
                    },
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null,
                )
            }
        }
    }
}
