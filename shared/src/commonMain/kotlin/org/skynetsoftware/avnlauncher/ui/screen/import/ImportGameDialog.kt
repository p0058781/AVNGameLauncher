package org.skynetsoftware.avnlauncher.ui.screen.import

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.ui.screen.Dialog
import org.skynetsoftware.avnlauncher.ui.viewmodel.ImportGameViewModel
import org.skynetsoftware.avnlauncher.ui.viewmodel.MainViewModel

@Composable
fun ImportGameDialog(
    importGameViewModel: ImportGameViewModel = koinInject(),
    mainViewModel: MainViewModel = koinInject(),
    onCloseRequest: () -> Unit = {},
) {
    var threadId by remember { importGameViewModel.threadId }.collectAsMutableState()
    val state by remember { importGameViewModel.state }.collectAsState()

    Dialog(
        title = stringResource(MR.strings.importGameDialogTitle),
        onDismiss = onCloseRequest,
    ) {
        Surface(
            modifier = Modifier,
        ) {
            val stateCopy = state
            when (stateCopy) {
                ImportGameViewModel.State.Idle -> {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = threadId ?: "",
                            onValueChange = {
                                threadId = it
                            },
                            label = { Text(stringResource(MR.strings.importGameDialogThreadIdHint)) },
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = threadId.isNullOrBlank().not(),
                            onClick = {
                                importGameViewModel.import()
                            },
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Text(
                                text = stringResource(MR.strings.importGameDialogButtonImport),
                            )
                        }
                    }
                }

                is ImportGameViewModel.State.Imported -> {
                    mainViewModel.showToast(stringResource(MR.strings.importGameDialogSuccessToast, stateCopy.game.title))
                    onCloseRequest()
                }

                ImportGameViewModel.State.Importing -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ImportGameViewModel.State.Error -> {
                    mainViewModel.showToast(stringResource(MR.strings.importGameDialogErrorToast, stateCopy.error.message ?: ""))
                    onCloseRequest()
                }
            }
        }
    }
}
