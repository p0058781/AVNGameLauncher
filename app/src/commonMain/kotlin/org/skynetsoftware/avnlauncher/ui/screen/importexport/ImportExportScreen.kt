package org.skynetsoftware.avnlauncher.ui.screen.importexport

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.app.generated.resources.*
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportButtonExport
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportButtonImport
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportItemDescriptionClickToChange
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportItemDescriptionExportFileName
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportItemDescriptionExportGames
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportItemDescriptionExportSettings
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportItemDescriptionImportGames
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportItemDescriptionImportSettings
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportItemTitleExportDir
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportItemTitleExportFileName
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportItemTitleExportGames
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportItemTitleExportSettings
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportItemTitleImportFilePath
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportItemTitleImportGames
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportItemTitleImportSettings
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportSectionTitleExport
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportSectionTitleImport
import org.skynetsoftware.avnlauncher.ui.component.Input
import org.skynetsoftware.avnlauncher.ui.component.Item
import org.skynetsoftware.avnlauncher.ui.component.Section
import org.skynetsoftware.avnlauncher.ui.component.Toggle
import org.skynetsoftware.avnlauncher.utils.collectAsMutableState
import org.skynetsoftware.avnlauncher.utils.hoursToMilliseconds
import org.skynetsoftware.avnlauncher.utils.millisecondsToHours

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ImportExportScreen(
    importExportViewModel: ImportExportViewModel = koinInject()
) {
    var importFile by remember { importExportViewModel.importFile }.collectAsMutableState()
    var importGames by remember { importExportViewModel.importGames }.collectAsMutableState()
    var importSettings by remember { importExportViewModel.importSettings }.collectAsMutableState()

    var exportDir by remember { importExportViewModel.exportDir }.collectAsMutableState()
    var exportFileName by remember { importExportViewModel.exportFileName }.collectAsMutableState()
    var exportGames by remember { importExportViewModel.exportGames }.collectAsMutableState()
    var exportSettings by remember { importExportViewModel.exportSettings }.collectAsMutableState()

    var dirPickerVisible by remember { mutableStateOf(false) }
    var importFilePickerVisible by remember { mutableStateOf(false) }

    DirectoryPicker(dirPickerVisible, exportDir) { path ->
        exportDir = path
        dirPickerVisible = false
    }

    FilePicker(importFilePickerVisible, importFile) { path ->
        importFile = path?.path
        importFilePickerVisible = false
    }

    Surface(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
        ) {

            Section(
                title = stringResource(Res.string.importExportSectionTitleImport)
            ) {
                Item(
                    title = stringResource(Res.string.importExportItemTitleImportFilePath),
                    subtitle = if (importFile.isNullOrBlank()) {
                        stringResource(Res.string.importExportItemDescriptionClickToChange)
                    } else {
                        importFile!!
                    },
                    onClick = {
                        importFilePickerVisible = true
                    }
                )
                Item(
                    title = stringResource(Res.string.importExportItemTitleImportGames),
                    subtitle = stringResource(Res.string.importExportItemDescriptionImportGames),
                    endContent = {
                        Toggle(
                            checked = importGames
                        ) {
                            importGames = it
                        }
                    }
                )
                Item(
                    title = stringResource(Res.string.importExportItemTitleImportSettings),
                    subtitle = stringResource(Res.string.importExportItemDescriptionImportSettings),
                    endContent = {
                        Toggle(
                            checked = importSettings
                        ) {
                            importSettings = it
                        }
                    }
                )
                Button(
                    modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
                    onClick = {
                        importExportViewModel.import()
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.importExportButtonImport)
                    )
                }

            }
            Spacer(
                modifier = Modifier.height(10.dp)
            )
            Section(
                title = stringResource(Res.string.importExportSectionTitleExport)
            ) {
                Item(
                    title = stringResource(Res.string.importExportItemTitleExportDir),
                    subtitle = if(exportDir.isNullOrBlank()) {
                        stringResource(Res.string.importExportItemDescriptionClickToChange)
                    } else {
                        exportDir!!
                    },
                    onClick = {
                        dirPickerVisible = true
                    }
                )
                Item(
                    title = stringResource(Res.string.importExportItemTitleExportFileName),
                    subtitle = stringResource(Res.string.importExportItemDescriptionExportFileName),
                    endContent = {
                        Input(
                            hint = stringResource(Res.string.importExportItemHintExportFileName),
                            currentValue = exportFileName,
                            validateInput = { true },
                            onSaveClicked = {
                                exportFileName = it
                            },
                        )
                    }
                )
                Item(
                    title = stringResource(Res.string.importExportItemTitleExportGames),
                    subtitle = stringResource(Res.string.importExportItemDescriptionExportGames),
                    endContent = {
                        Toggle(
                            checked = exportGames
                        ) {
                            exportGames = it
                        }
                    }
                )
                Item(
                    title = stringResource(Res.string.importExportItemTitleExportSettings),
                    subtitle = stringResource(Res.string.importExportItemDescriptionExportSettings),
                    endContent = {
                        Toggle(
                            checked = exportSettings
                        ) {
                            exportSettings = it
                        }
                    }
                )
                Button(
                    modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
                    onClick = {
                        importExportViewModel.export()
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.importExportButtonExport)
                    )
                }
            }
        }
    }
}
