package org.skynetsoftware.avnlauncher.ui.screen.customlists

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
import org.skynetsoftware.avnlauncher.app.generated.resources.customListsAddNew
import org.skynetsoftware.avnlauncher.app.generated.resources.customListsDeleteCancelButton
import org.skynetsoftware.avnlauncher.app.generated.resources.customListsDeleteConfirmButton
import org.skynetsoftware.avnlauncher.app.generated.resources.customListsDeleteText
import org.skynetsoftware.avnlauncher.app.generated.resources.customListsDeleteTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.customListsInputHintDescription
import org.skynetsoftware.avnlauncher.app.generated.resources.customListsInputHintLabel
import org.skynetsoftware.avnlauncher.app.generated.resources.customListsInputLabelDescription
import org.skynetsoftware.avnlauncher.app.generated.resources.customListsInputLabelLabel
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModel

@Composable
fun CustomListsScreen(customListsViewModel: CustomListsViewModel = viewModel()) {
    val gamesLists by remember { customListsViewModel.gamesLists }.collectAsState(emptyList())
    var confirmDeleteGamesList by remember { mutableStateOf<GamesListViewItem?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column {
            TextButton(
                onClick = {
                    customListsViewModel.add()
                },
            ) {
                Text(
                    text = stringResource(Res.string.customListsAddNew),
                )
            }
            Divider()
            LazyColumn {
                items(gamesLists) { gamesList ->
                    ListItem(
                        gamesList = gamesList,
                        edit = {
                            customListsViewModel.edit(it)
                        },
                        delete = {
                            confirmDeleteGamesList = it
                        },
                        save = { playStateItem, label, description ->
                            customListsViewModel.save(playStateItem, label, description)
                        },
                    )
                    Divider()
                }
            }
        }
    }

    confirmDeleteGamesList?.let {
        AlertDialog(
            onDismissRequest = {
                confirmDeleteGamesList = null
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmDeleteGamesList = null
                        customListsViewModel.delete(it)
                    },
                ) {
                    Text(
                        text = stringResource(Res.string.customListsDeleteConfirmButton),
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        confirmDeleteGamesList = null
                    },
                ) {
                    Text(
                        text = stringResource(Res.string.customListsDeleteCancelButton),
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(Res.string.customListsDeleteTitle, it.name),
                )
            },
            text = {
                Text(
                    text = stringResource(Res.string.customListsDeleteText),
                )
            },
        )
    }
}

@Composable
private fun ListItem(
    gamesList: GamesListViewItem,
    edit: (gamesList: GamesListViewItem) -> Unit,
    delete: (gamesList: GamesListViewItem) -> Unit,
    save: (gamesList: GamesListViewItem, name: String, description: String?) -> Unit,
) {
    Box(
        modifier = Modifier.padding(10.dp).fillMaxWidth(),
    ) {
        if (gamesList.editing) {
            var name by remember { mutableStateOf(gamesList.name) }
            var description by remember { mutableStateOf(gamesList.description) }
            Row {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp).fillMaxWidth(),
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        label = {
                            Text(
                                text = stringResource(Res.string.customListsInputLabelLabel),
                            )
                        },
                        placeholder = {
                            Text(
                                text = stringResource(Res.string.customListsInputHintLabel),
                            )
                        },
                        isError = gamesList.error != null,
                    )
                    gamesList.error?.let {
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
                                text = stringResource(Res.string.customListsInputLabelDescription),
                            )
                        },
                        placeholder = {
                            Text(
                                text = stringResource(Res.string.customListsInputHintDescription),
                            )
                        },
                    )
                }
                Spacer(
                    modifier = Modifier.width(10.dp),
                )
                Icon(
                    modifier = Modifier.align(Alignment.CenterVertically).clickable {
                        save(gamesList, name, description)
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
                        text = gamesList.name,
                        color = MaterialTheme.colors.onPrimary,
                    )
                    gamesList.description?.let {
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
                        edit(gamesList)
                    },
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                )
                Icon(
                    modifier = Modifier.align(Alignment.CenterVertically).clickable {
                        delete(gamesList)
                    },
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null,
                )
            }
        }
    }
}
