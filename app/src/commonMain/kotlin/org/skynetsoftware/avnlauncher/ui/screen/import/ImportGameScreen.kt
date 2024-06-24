package org.skynetsoftware.avnlauncher.ui.screen.import

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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.LocalNavigator
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.import
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogButtonImport
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogErrorToast
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogFirstPlayedHint
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogFirstPlayedInvalid
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogGameExists
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogInvalidUrl
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogPlayTimeHint
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogPlayTimeInvalid
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogSuccessToast
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogThreadIdHint
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameScreenButtonAddCustomGame
import org.skynetsoftware.avnlauncher.domain.usecase.GameExistsException
import org.skynetsoftware.avnlauncher.domain.usecase.InvalidUrlException
import org.skynetsoftware.avnlauncher.ui.input.DateVisualTransformation
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModel
import org.skynetsoftware.avnlauncher.utils.collectAsMutableState

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
            ImportGameViewModel.State.Idle,
            ImportGameViewModel.State.Importing,
            -> {
                Column(
                    modifier = Modifier.padding(10.dp),
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = threadId.orEmpty(),
                        onValueChange = {
                            threadId = it
                        },
                        label = { Text(stringResource(Res.string.importGameDialogThreadIdHint)) },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = playTime ?: "",
                        onValueChange = {
                            playTime = it.filter { it.isDigit() }
                        },
                        label = { Text(stringResource(Res.string.importGameDialogPlayTimeHint)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = firstPlayed ?: "",
                        onValueChange = {
                            firstPlayed = it
                                .filter { it.isDigit() }
                                .take(DateVisualTransformation.CHAR_COUNT_WITHOUT_DIVIDER)
                        },
                        label = { Text(stringResource(Res.string.importGameDialogFirstPlayedHint)) },
                        placeholder = { Text(DateVisualTransformation.MASK) },
                        visualTransformation = DateVisualTransformation,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    val navigator = LocalNavigator.current
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navigator?.navigateToCreateCustomGame()
                            onCloseRequest()
                        },
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(
                            text = stringResource(Res.string.importGameScreenButtonAddCustomGame),
                        )
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = threadId.isNullOrBlank().not() && state is ImportGameViewModel.State.Idle,
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

            is ImportGameViewModel.State.Error -> {
                val message = when (stateCopy.error) {
                    is GameExistsException -> stringResource(Res.string.importGameDialogGameExists)
                    is InvalidUrlException -> stringResource(Res.string.importGameDialogInvalidUrl)
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
