package org.skynetsoftware.avnlauncher.ui.screen.import

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogButtonImport
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogErrorToast
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogFirstPlayedHint
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogFirstPlayedInvalid
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogFirstPlayedPlaceholder
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogGameExists
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogInvalidUrl
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogPlayTimeHint
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogPlayTimeInvalid
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogSuccessToast
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogThreadIdHint
import org.skynetsoftware.avnlauncher.data.GameImport
import org.skynetsoftware.avnlauncher.ui.input.DateVisualTransformation
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModel
import org.skynetsoftware.avnlauncher.utils.collectAsMutableState

const val FIRST_PLAYED_DATE_FORMAT = "dd/MM/yyyy"
const val FIRST_PLAYED_DIVIDER = '/'
const val FIRST_PLAYED_DATE_CHAR_COUNT = 8

@OptIn(ExperimentalResourceApi::class)
@Suppress("LongMethod")
@Composable
fun ImportGameScreen(
    importGameViewModel: ImportGameViewModel = viewModel(),
    onCloseRequest: () -> Unit,
) {
    var threadId by remember { importGameViewModel.threadId }
        .collectAsMutableState(context = Dispatchers.Main.immediate)
    var playTime by remember { importGameViewModel.playTime }
        .collectAsMutableState(context = Dispatchers.Main.immediate)
    var firstPlayed by remember { importGameViewModel.firstPlayed }
        .collectAsMutableState(context = Dispatchers.Main.immediate)
    val state by remember { importGameViewModel.state }.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    ) {
        when (val stateCopy = state) {
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
                        label = { Text(stringResource(Res.string.importGameDialogThreadIdHint)) },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = playTime ?: "",
                        onValueChange = {
                            playTime = it.filter { it.isDigit() }
                        },
                        label = { Text(stringResource(Res.string.importGameDialogPlayTimeHint)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = firstPlayed ?: "",
                        onValueChange = {
                            firstPlayed = it.filter { it.isDigit() }.take(FIRST_PLAYED_DATE_CHAR_COUNT)
                        },
                        label = { Text(stringResource(Res.string.importGameDialogFirstPlayedHint)) },
                        placeholder = { Text(stringResource(Res.string.importGameDialogFirstPlayedPlaceholder)) },
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
                            importGameViewModel.import()
                        },
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(
                            text = stringResource(Res.string.importGameDialogButtonImport),
                        )
                    }
                }
            }

            is ImportGameViewModel.State.Imported -> {
                importGameViewModel.showToast(
                    stringResource(Res.string.importGameDialogSuccessToast, stateCopy.game.title),
                )
                onCloseRequest()
            }

            ImportGameViewModel.State.Importing -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CircularProgressIndicator()
                }
            }

            is ImportGameViewModel.State.Error -> {
                val message = when (stateCopy.error) {
                    is GameImport.GameExistsException -> stringResource(Res.string.importGameDialogGameExists)
                    is GameImport.InvalidUrlException -> stringResource(Res.string.importGameDialogInvalidUrl)
                    is ImportGameViewModel.ValidationFirstPlayedInvalidException -> stringResource(
                        Res.string.importGameDialogFirstPlayedInvalid,
                    )
                    is ImportGameViewModel.ValidationPlayTimeInvalidException -> stringResource(
                        Res.string.importGameDialogPlayTimeInvalid,
                    )
                    else -> stateCopy.error.message.orEmpty()
                }
                importGameViewModel.showToast(
                    message = stringResource(
                        Res.string.importGameDialogErrorToast,
                        message,
                    ),
                )
                importGameViewModel.resetState()
            }
        }
    }
}
