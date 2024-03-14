package org.skynetsoftware.avnlauncher.ui.screen.import

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.data.GameImport
import org.skynetsoftware.avnlauncher.ui.input.DateVisualTransformation
import org.skynetsoftware.avnlauncher.ui.screen.Dialog
import org.skynetsoftware.avnlauncher.ui.screen.applyPlatformSpecificHeight
import org.skynetsoftware.avnlauncher.ui.viewmodel.ImportGameScreenModel
import org.skynetsoftware.avnlauncher.ui.viewmodel.MainScreenModel
import org.skynetsoftware.avnlauncher.utils.collectAsMutableState

const val FIRST_PLAYED_DATE_FORMAT = "dd/MM/yyyy"
const val FIRST_PLAYED_DIVIDER = '/'
const val FIRST_PLAYED_DATE_CHAR_COUNT = 8

@Suppress("LongMethod")
@Composable
fun ImportGameDialog(
    importGameScreenModel: ImportGameScreenModel = koinInject(),
    mainScreenModel: MainScreenModel = koinInject(),
    onCloseRequest: () -> Unit = {},
) {
    var threadId by remember { importGameScreenModel.threadId }
        .collectAsMutableState(context = Dispatchers.Main.immediate)
    var playTime by remember { importGameScreenModel.playTime }
        .collectAsMutableState(context = Dispatchers.Main.immediate)
    var firstPlayed by remember { importGameScreenModel.firstPlayed }
        .collectAsMutableState(context = Dispatchers.Main.immediate)
    val state by remember { importGameScreenModel.state }.collectAsState()

    Dialog(
        title = stringResource(MR.strings.importGameDialogTitle),
        onDismiss = onCloseRequest,
    ) {
        Surface(
            modifier = Modifier.applyPlatformSpecificHeight().verticalScroll(rememberScrollState()),
        ) {
            when (val stateCopy = state) {
                ImportGameScreenModel.State.Idle -> {
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
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = playTime ?: "",
                            onValueChange = {
                                playTime = it.filter { it.isDigit() }
                            },
                            label = { Text(stringResource(MR.strings.importGameDialogPlayTimeHint)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = firstPlayed ?: "",
                            onValueChange = {
                                firstPlayed = it.filter { it.isDigit() }.take(FIRST_PLAYED_DATE_CHAR_COUNT)
                            },
                            label = { Text(stringResource(MR.strings.importGameDialogFirstPlayedHint)) },
                            placeholder = { Text(stringResource(MR.strings.importGameDialogFirstPlayedPlaceholder)) },
                            visualTransformation = DateVisualTransformation(
                                FIRST_PLAYED_DATE_FORMAT,
                                FIRST_PLAYED_DIVIDER,
                            ),
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = threadId.isNullOrBlank().not(),
                            onClick = {
                                importGameScreenModel.import()
                            },
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Text(
                                text = stringResource(MR.strings.importGameDialogButtonImport),
                            )
                        }
                    }
                }

                is ImportGameScreenModel.State.Imported -> {
                    mainScreenModel.showToast(
                        stringResource(MR.strings.importGameDialogSuccessToast, stateCopy.game.title),
                    )
                    onCloseRequest()
                }

                ImportGameScreenModel.State.Importing -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ImportGameScreenModel.State.Error -> {
                    val message = when (stateCopy.error) {
                        is GameImport.GameExistsException -> stringResource(MR.strings.importGameDialogGameExists)
                        is GameImport.InvalidUrlException -> stringResource(MR.strings.importGameDialogInvalidUrl)
                        is ImportGameScreenModel.ValidationFirstPlayedInvalidException -> stringResource(
                            MR.strings.importGameDialogFirstPlayedInvalid,
                        )
                        is ImportGameScreenModel.ValidationPlayTimeInvalidException -> stringResource(
                            MR.strings.importGameDialogPlayTimeInvalid,
                        )
                        else -> stateCopy.error.message.orEmpty()
                    }
                    mainScreenModel.showToast(
                        message = stringResource(
                            MR.strings.importGameDialogErrorToast,
                            message,
                        ),
                    )
                    importGameScreenModel.resetState()
                }
            }
        }
    }
}
