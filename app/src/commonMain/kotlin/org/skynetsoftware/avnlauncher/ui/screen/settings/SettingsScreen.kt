package org.skynetsoftware.avnlauncher.ui.screen.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.BuildKonfig
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.edit
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsDialogInputLabelGamesDir
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsDialogMinimizeToTrayOnClose
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsDialogMinimizeToTrayOnCloseDescription
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsDialogPeriodicUpdateChecks
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsDialogPeriodicUpdateChecksDescription
import org.skynetsoftware.avnlauncher.app.generated.resources.versionTemplate
import org.skynetsoftware.avnlauncher.domain.utils.Option
import org.skynetsoftware.avnlauncher.ui.component.CheckBoxWithText
import org.skynetsoftware.avnlauncher.ui.screen.GamesDirPicker
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModel

@Suppress("LongMethod")
@OptIn(ExperimentalResourceApi::class)
@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel = viewModel()) {
    val periodicUpdateChecks by remember { settingsViewModel.periodicUpdateChecksEnabled }.collectAsState()
    val gamesDirShown = settingsViewModel.gamesDir is Option.Some
    val minimizeToTrayOnCloseShown = settingsViewModel.minimizeToTrayOnClose is Option.Some

    Surface(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    ) {
        var showFilePicker by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.versionTemplate, BuildKonfig.version),
                style = MaterialTheme.typography.body2,
            )
            Spacer(
                modifier = Modifier.height(10.dp),
            )
            if (gamesDirShown) {
                val gamesDir by remember { (settingsViewModel.gamesDir as Option.Some).value }.collectAsState()
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = gamesDir ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(Res.string.settingsDialogInputLabelGamesDir)) },
                    trailingIcon = {
                        Image(
                            painter = painterResource(Res.drawable.edit),
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
            Spacer(modifier = Modifier.height(10.dp))
            CheckBoxWithText(
                text = stringResource(Res.string.settingsDialogPeriodicUpdateChecks),
                checked = periodicUpdateChecks,
                onCheckedChange = {
                    settingsViewModel.setPeriodicUpdateChecks(it)
                },
                description = stringResource(Res.string.settingsDialogPeriodicUpdateChecksDescription),
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (minimizeToTrayOnCloseShown) {
                val minimizeToTrayOnClose by remember {
                    (settingsViewModel.minimizeToTrayOnClose as Option.Some).value
                }.collectAsState()
                CheckBoxWithText(
                    text = stringResource(Res.string.settingsDialogMinimizeToTrayOnClose),
                    checked = minimizeToTrayOnClose,
                    onCheckedChange = {
                        settingsViewModel.setMinimizeToTrayOnClose(it)
                    },
                    description = stringResource(Res.string.settingsDialogMinimizeToTrayOnCloseDescription),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
