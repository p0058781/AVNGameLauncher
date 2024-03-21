package org.skynetsoftware.avnlauncher.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.BuildKonfig
import org.skynetsoftware.avnlauncher.LocalNavigator
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionCustomLists
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionCustomStatuses
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionDateFormat
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionGamesDir
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionGridCardValues
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionGridColumns
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionGridImageAspectRatio
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionImportExport
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionLogLevel
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionMinimizeToTray
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionShowGifs
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionStartMinimized
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionTimeFormat
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionUpdateCheckInterval
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionUpdateChecks
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemDescriptionUpdateNotifications
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemHintDateFormat
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemHintGridImageAspectRatio
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemHintTimeFormat
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemHintUpdateCheckInterval
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleCustomLists
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleCustomStatuses
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleDateFormat
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleF95Thread
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleGameCardValues
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleGamesDir
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleGridColumns
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleGridImageAspectRatio
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleImportExport
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleLogLevel
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleMinimizeToTray
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleShowGifs
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleSource
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleStartMinimized
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleTimeFormat
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleUpdateCheckInterval
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleUpdateChecks
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleUpdateNotifications
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsItemTitleVersion
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsSectionTitleAbout
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsSectionTitleGames
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsSectionTitleGeneral
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsSectionTitleUI
import org.skynetsoftware.avnlauncher.domain.model.GridColumns
import org.skynetsoftware.avnlauncher.domain.model.LogLevel
import org.skynetsoftware.avnlauncher.domain.utils.Option
import org.skynetsoftware.avnlauncher.link.ExternalLinkUtils
import org.skynetsoftware.avnlauncher.ui.screen.GamesDirPicker
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModel
import org.skynetsoftware.avnlauncher.utils.hoursToMilliseconds
import org.skynetsoftware.avnlauncher.utils.isValidDateTimeFormat
import org.skynetsoftware.avnlauncher.utils.millisecondsToHours
import java.net.URI

private const val GITHUB_URL = "https://github.com/p0058781/AVNGameLauncher"
private const val F95_THREAD_URL = "https://f95zone.to/threads/avn-game-launcher-1-1-0.198164/"

@Suppress("LongMethod")
@OptIn(ExperimentalResourceApi::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(),
    externalLinkUtils: ExternalLinkUtils = koinInject(),
) {
    val periodicUpdateChecks by remember { settingsViewModel.periodicUpdateChecksEnabled }.collectAsState()
    val gamesDirShown = settingsViewModel.gamesDir is Option.Some
    val minimizeToTrayOnCloseShown = settingsViewModel.minimizeToTrayOnClose is Option.Some
    val startMinimizedShown = settingsViewModel.startMinimized is Option.Some
    val logLevel by remember { settingsViewModel.logLevel }.collectAsState()
    val showGifs by remember { settingsViewModel.showGifs }.collectAsState()
    val dateFormat by remember { settingsViewModel.dateFormat }.collectAsState()
    val timeFormat by remember { settingsViewModel.timeFormat }.collectAsState()
    val systemNotificationsEnabled by remember { settingsViewModel.systemNotificationsEnabled }.collectAsState()
    val gridColumns by remember { settingsViewModel.gridColumns }.collectAsState()
    val updateCheckInterval by remember { settingsViewModel.updateCheckInterval }.collectAsState()
    val gridImageAspectRatio by remember { settingsViewModel.gridImageAspectRatio }.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
        ) {
            val navigator = LocalNavigator.current
            Section(
                title = stringResource(Res.string.settingsSectionTitleGeneral),
            ) {
                if (minimizeToTrayOnCloseShown) {
                    val minimizeToTrayOnClose by remember {
                        (settingsViewModel.minimizeToTrayOnClose as Option.Some).value
                    }.collectAsState()
                    Item(
                        title = stringResource(Res.string.settingsItemTitleMinimizeToTray),
                        subtitle = stringResource(Res.string.settingsItemDescriptionMinimizeToTray),
                        endContent = {
                            Toggle(minimizeToTrayOnClose) {
                                settingsViewModel.setMinimizeToTrayOnClose(it)
                            }
                        },
                    )
                    Divider()
                }
                if (startMinimizedShown) {
                    val startMinimized by remember {
                        (settingsViewModel.startMinimized as Option.Some).value
                    }.collectAsState()
                    Item(
                        title = stringResource(Res.string.settingsItemTitleStartMinimized),
                        subtitle = stringResource(Res.string.settingsItemDescriptionStartMinimized),
                        endContent = {
                            Toggle(startMinimized) {
                                settingsViewModel.setStartMinimized(it)
                            }
                        },
                    )
                    Divider()
                }
                Item(
                    title = stringResource(Res.string.settingsItemTitleLogLevel),
                    subtitle = stringResource(Res.string.settingsItemDescriptionLogLevel),
                    endContent = {
                        Dropdown(
                            values = LogLevel.entries,
                            currentValue = logLevel,
                            onValueChanged = {
                                settingsViewModel.setLogLevel(it)
                            },
                        )
                    },
                )
                Divider()

                Item(
                    title = stringResource(Res.string.settingsItemTitleImportExport),
                    subtitle = stringResource(Res.string.settingsItemDescriptionImportExport),
                    onClick = {
                        navigator?.navigateToImportExport()
                    },
                )
            }
            Spacer(
                modifier = Modifier.height(10.dp),
            )
            Section(
                title = stringResource(Res.string.settingsSectionTitleGames),
            ) {
                if (gamesDirShown) {
                    val gamesDir by remember { (settingsViewModel.gamesDir as Option.Some).value }.collectAsState()
                    var showFilePicker by remember { mutableStateOf(false) }
                    Item(
                        title = stringResource(Res.string.settingsItemTitleGamesDir),
                        subtitle = gamesDir ?: stringResource(Res.string.settingsItemDescriptionGamesDir),
                        onClick = {
                            showFilePicker = true
                        },
                    )
                    Divider()
                    GamesDirPicker(showFilePicker, gamesDir) {
                        showFilePicker = false
                        it?.let {
                            settingsViewModel.setGamesDir(it)
                        }
                    }
                }
                Item(
                    title = stringResource(Res.string.settingsItemTitleUpdateChecks),
                    subtitle = stringResource(Res.string.settingsItemDescriptionUpdateChecks),
                    endContent = {
                        Toggle(periodicUpdateChecks) {
                            settingsViewModel.setPeriodicUpdateChecks(it)
                        }
                    },
                )
                Divider()
                Item(
                    title = stringResource(Res.string.settingsItemTitleUpdateCheckInterval),
                    subtitle = stringResource(Res.string.settingsItemDescriptionUpdateCheckInterval),
                    endContent = {
                        Input(
                            hint = stringResource(Res.string.settingsItemHintUpdateCheckInterval),
                            currentValue = updateCheckInterval.millisecondsToHours().toString(),
                            validateInput = {
                                val isNumber = it.all { char -> char.isDigit() }
                                it.isNotBlank() && isNumber && it.toInt() > 0
                            },
                            onSaveClicked = {
                                settingsViewModel.setUpdateCheckInterval(it.toInt().hoursToMilliseconds())
                            },
                        )
                    },
                )
                Divider()
                Item(
                    title = stringResource(Res.string.settingsItemTitleUpdateNotifications),
                    subtitle = stringResource(Res.string.settingsItemDescriptionUpdateNotifications),
                    endContent = {
                        Toggle(systemNotificationsEnabled) {
                            settingsViewModel.setSystemNotificationsEnabled(it)
                        }
                    },
                )
                Divider()
                Item(
                    title = stringResource(Res.string.settingsItemTitleCustomLists),
                    subtitle = stringResource(Res.string.settingsItemDescriptionCustomLists),
                    onClick = {
                        navigator?.navigateToCustomLists()
                    },
                )
                Divider()
                Item(
                    title = stringResource(Res.string.settingsItemTitleCustomStatuses),
                    subtitle = stringResource(Res.string.settingsItemDescriptionCustomStatuses),
                    onClick = {
                        navigator?.navigateToCustomStatuses()
                    },
                )
            }
            Section(
                title = stringResource(Res.string.settingsSectionTitleUI),
            ) {
                Item(
                    title = stringResource(Res.string.settingsItemTitleDateFormat),
                    subtitle = stringResource(Res.string.settingsItemDescriptionDateFormat),
                    endContent = {
                        Input(
                            hint = stringResource(Res.string.settingsItemHintDateFormat),
                            currentValue = dateFormat,
                            validateInput = {
                                it.isValidDateTimeFormat()
                            },
                            onSaveClicked = {
                                settingsViewModel.setDateFormat(it)
                            },
                        )
                    },
                )
                Divider()
                Item(
                    title = stringResource(Res.string.settingsItemTitleTimeFormat),
                    subtitle = stringResource(Res.string.settingsItemDescriptionTimeFormat),
                    endContent = {
                        Input(
                            hint = stringResource(Res.string.settingsItemHintTimeFormat),
                            currentValue = timeFormat,
                            validateInput = {
                                it.isValidDateTimeFormat()
                            },
                            onSaveClicked = {
                                settingsViewModel.setTimeFormat(it)
                            },
                        )
                    },
                )
                Divider()
                Item(
                    title = stringResource(Res.string.settingsItemTitleGridColumns),
                    subtitle = stringResource(Res.string.settingsItemDescriptionGridColumns),
                    endContent = {
                        Dropdown(
                            values = GridColumns.entries,
                            currentValue = gridColumns,
                            onValueChanged = {
                                settingsViewModel.setGridColumns(it)
                            },
                        )
                    },
                )
                Divider()
                Item(
                    title = stringResource(Res.string.settingsItemTitleGridImageAspectRatio),
                    subtitle = stringResource(Res.string.settingsItemDescriptionGridImageAspectRatio),
                    endContent = {
                        Input(
                            hint = stringResource(Res.string.settingsItemHintGridImageAspectRatio),
                            currentValue = gridImageAspectRatio.toString(),
                            validateInput = {
                                it.toFloatOrNull() != null
                            },
                            onSaveClicked = {
                                settingsViewModel.setGridImageAspectRatio(it.toFloat())
                            },
                        )
                    },
                )
                Divider()
                Item(
                    title = stringResource(Res.string.settingsItemTitleGameCardValues),
                    subtitle = stringResource(Res.string.settingsItemDescriptionGridCardValues),
                    onClick = {
                        navigator?.navigateToCardValues()
                    },
                )
                Divider()
                Item(
                    title = stringResource(Res.string.settingsItemTitleShowGifs),
                    subtitle = stringResource(Res.string.settingsItemDescriptionShowGifs),
                    endContent = {
                        Toggle(showGifs) {
                            settingsViewModel.setShowGifs(it)
                        }
                    },
                )
            }
            Section(
                title = stringResource(Res.string.settingsSectionTitleAbout),
            ) {
                Item(
                    title = stringResource(Res.string.settingsItemTitleVersion),
                    subtitle = BuildKonfig.version,
                )
                Divider()
                Item(
                    title = stringResource(Res.string.settingsItemTitleSource),
                    subtitle = GITHUB_URL,
                    onClick = {
                        externalLinkUtils.openInBrowser(URI(GITHUB_URL))
                    },
                )
                Divider()
                Item(
                    title = stringResource(Res.string.settingsItemTitleF95Thread),
                    subtitle = F95_THREAD_URL,
                    onClick = {
                        externalLinkUtils.openInBrowser(URI(F95_THREAD_URL))
                    },
                )
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.subtitle1,
        )
        Spacer(
            modifier = Modifier.height(5.dp),
        )
        Card {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun Item(
    title: String,
    subtitle: String = "",
    endContent: @Composable RowScope.() -> Unit = {},
    onClick: (() -> Unit)? = null,
) {
    val modifier = Modifier.run {
        if (onClick != null) {
            clickable {
                onClick()
            }
        } else {
            this
        }
    }
    Row(
        modifier = modifier.padding(10.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                color = MaterialTheme.colors.onPrimary,
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.body2,
                )
            }
        }
        endContent()
    }
}

@Composable
private fun RowScope.Toggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(
        modifier = Modifier.align(Alignment.CenterVertically),
        checked = checked,
        onCheckedChange = onCheckedChange,
    )
}

@Composable
private fun <T> RowScope.Dropdown(
    values: List<T>,
    currentValue: T,
    onValueChanged: (newValue: T) -> Unit,
) {
    var dropdownShown by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.align(Alignment.CenterVertically).clickable {
            dropdownShown = true
        },
    ) {
        Text(
            text = currentValue.toString(),
        )
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
        )
        DropdownMenu(
            expanded = dropdownShown,
            onDismissRequest = {
                dropdownShown = false
            },
        ) {
            values.forEach {
                DropdownMenuItem(
                    onClick = {
                        dropdownShown = false
                        onValueChanged(it)
                    },
                ) {
                    Text(
                        text = it.toString(),
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.Input(
    currentValue: String,
    hint: String,
    validateInput: (value: String) -> Boolean,
    onSaveClicked: (value: String) -> Unit,
) {
    var value by remember { mutableStateOf(currentValue) }

    val textStyle = MaterialTheme.typography.body2
    val colors = TextFieldDefaults.outlinedTextFieldColors()
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(true).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
    val interactionSource = remember { MutableInteractionSource() }
    val shape = MaterialTheme.shapes.small

    Row(
        modifier = Modifier.align(Alignment.CenterVertically),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        @OptIn(ExperimentalMaterialApi::class)
        BasicTextField(
            value = value,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .background(colors.backgroundColor(true).value, shape)
                .defaultMinSize(
                    minWidth = 140.dp,
                    minHeight = 42.dp,
                ),
            onValueChange = {
                value = it
            },
            cursorBrush = SolidColor(colors.cursorColor(false).value),
            textStyle = mergedTextStyle,
            interactionSource = interactionSource,
            decorationBox = @Composable { innerTextField ->
                TextFieldDefaults.OutlinedTextFieldDecorationBox(
                    value = value,
                    visualTransformation = VisualTransformation.None,
                    innerTextField = innerTextField,
                    placeholder = {
                        Text(
                            text = hint,
                            style = textStyle,
                        )
                    },
                    singleLine = true,
                    enabled = true,
                    interactionSource = interactionSource,
                    colors = colors,
                    border = {
                        TextFieldDefaults.BorderBox(
                            enabled = true,
                            isError = false,
                            interactionSource,
                            colors,
                            shape,
                        )
                    },
                    contentPadding = PaddingValues(8.dp),
                )
            },
        )
        if (validateInput(value) && value.isNotBlank() && value != currentValue) {
            Spacer(
                modifier = Modifier.width(10.dp),
            )
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.clickable {
                    onSaveClicked(value)
                },
            )
        }
    }
}
