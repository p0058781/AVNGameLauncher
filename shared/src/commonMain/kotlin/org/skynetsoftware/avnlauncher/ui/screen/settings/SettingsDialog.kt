package org.skynetsoftware.avnlauncher.ui.screen.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import org.skynetsoftware.avnlauncher.ui.component.CheckBoxWithText
import org.skynetsoftware.avnlauncher.ui.screen.Dialog
import org.skynetsoftware.avnlauncher.ui.screen.GamesDirPicker
import org.skynetsoftware.avnlauncher.ui.viewmodel.SettingsViewModel
import org.skynetsoftware.avnlauncher.utils.Option

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SettingsDialog(
    settingsViewModel: SettingsViewModel = koinInject(),
    onCloseRequest: () -> Unit = {},
) {
    val syncEnabled by remember { settingsViewModel.syncEnabled }.collectAsState()
    val fastUpdateCheck by remember { settingsViewModel.fastUpdateCheck }.collectAsState()
    val forceDarkTheme by remember { settingsViewModel.forceDarkTheme }.collectAsState()
    val gamesDirShown = settingsViewModel.gamesDir is Option.Some

    Dialog(
        title = stringResource(MR.strings.settings),
        onDismiss = onCloseRequest,
    ) {
        Surface(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            var showFilePicker by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (gamesDirShown) {
                    val gamesDir by remember { (settingsViewModel.gamesDir as Option.Some).value }.collectAsState()
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = gamesDir ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(MR.strings.settingsDialogInputLabelGamesDir)) },
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

                    GamesDirPicker(showFilePicker, gamesDir) {
                        showFilePicker = false
                        it?.let {
                            settingsViewModel.setGamesDir(it)
                        }
                    }
                }
                CheckBoxWithText(
                    text = stringResource(MR.strings.settingsDialogSyncEnabled),
                    checked = syncEnabled,
                    onCheckedChange = {
                        settingsViewModel.setSyncEnabled(it)
                    },
                    description = stringResource(MR.strings.settingsDialogSyncEnabledDescription),
                )
                Spacer(modifier = Modifier.height(10.dp))
                CheckBoxWithText(
                    text = stringResource(MR.strings.settingsDialogFastUpdateCheck),
                    checked = fastUpdateCheck,
                    onCheckedChange = {
                        settingsViewModel.setFastUpdateCheck(it)
                    },
                    description = stringResource(MR.strings.settingsDialogFastUpdateCheckDescription),
                )
                Spacer(modifier = Modifier.height(10.dp))
                CheckBoxWithText(
                    text = stringResource(MR.strings.settingsDialogForceDarkTheme),
                    checked = forceDarkTheme,
                    onCheckedChange = {
                        settingsViewModel.setForceDarkTheme(it)
                    },
                    description = stringResource(MR.strings.settingsDialogForceDarkThemeDescription),
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
