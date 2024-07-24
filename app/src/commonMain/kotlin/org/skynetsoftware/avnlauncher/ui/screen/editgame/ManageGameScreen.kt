package org.skynetsoftware.avnlauncher.ui.screen.editgame

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dokar.chiptextfield.Chip
import com.dokar.chiptextfield.OutlinedChipTextField
import com.dokar.chiptextfield.rememberChipTextFieldState
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import org.skynetsoftware.avnlauncher.LocalWindowControl
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameButtonSave
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameInputError
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameInputLabelFirstReleaseDate
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameInputLabelImageUrl
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameInputLabelNotes
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameInputLabelReleaseDate
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameInputLabelTags
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameInputLabelTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameInputLabelVersion
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenItemDescriptionArchived
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenItemDescriptionCheckForUpdates
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenItemDescriptionLists
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenItemDescriptionPlayState
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenItemTitleArchived
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenItemTitleCheckForUpdates
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenItemTitleLists
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenItemTitlePlayState
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenNoExecutablePaths1
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenNoExecutablePaths2
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenNoExecutablePaths3
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenSectionTitleExecutablePaths
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenSectionTitleGameDetails
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenSectionTitleOptions
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameToastGameCreated
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameToastGameUpdated
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationAddExecutable
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationSearchExecutable
import org.skynetsoftware.avnlauncher.domain.utils.OS
import org.skynetsoftware.avnlauncher.domain.utils.os
import org.skynetsoftware.avnlauncher.filepicker.PickerType
import org.skynetsoftware.avnlauncher.filepicker.rememberFilePickerLauncher
import org.skynetsoftware.avnlauncher.ui.component.Dropdown
import org.skynetsoftware.avnlauncher.ui.component.HoverExplanation
import org.skynetsoftware.avnlauncher.ui.component.Item
import org.skynetsoftware.avnlauncher.ui.component.MultiselectDropdown
import org.skynetsoftware.avnlauncher.ui.component.Section
import org.skynetsoftware.avnlauncher.ui.component.Toggle
import org.skynetsoftware.avnlauncher.ui.input.DateVisualTransformation
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModel
import org.skynetsoftware.avnlauncher.utils.collectAsMutableState
import org.skynetsoftware.avnlauncher.utils.collectIsHoveredAsStateDelayed

@Composable
fun EditGameScreen(
    gameId: Int,
    onCloseRequest: () -> Unit,
) {
    ManageGameScreen(
        mode = ManageGameViewModel.Mode.EditGame(gameId),
        scrollable = false,
        onCloseRequest = onCloseRequest,
    )
}

@Composable
fun CreateCustomGameScreen(onCloseRequest: () -> Unit) {
    ManageGameScreen(
        mode = ManageGameViewModel.Mode.CreateCustomGame,
        scrollable = true,
        onCloseRequest = onCloseRequest,
    )
}

@Suppress("LongMethod")
@Composable
private fun ManageGameScreen(
    mode: ManageGameViewModel.Mode,
    manageGameViewModel: ManageGameViewModel = viewModel { parametersOf(mode) },
    scrollable: Boolean,
    onCloseRequest: () -> Unit,
) {
    var title by remember { manageGameViewModel.title }.collectAsMutableState(context = Dispatchers.Main.immediate)
    var imageUrl by remember { manageGameViewModel.imageUrl }
        .collectAsMutableState(context = Dispatchers.Main.immediate)
    var notes by remember { manageGameViewModel.notes }
        .collectAsMutableState(context = Dispatchers.Main.immediate)
    var version by remember { manageGameViewModel.version }
        .collectAsMutableState(context = Dispatchers.Main.immediate)
    var releaseDate by remember { manageGameViewModel.releaseDate }
        .collectAsMutableState(context = Dispatchers.Main.immediate)
    var firstReleaseDate by remember { manageGameViewModel.firstReleaseDate }
        .collectAsMutableState(context = Dispatchers.Main.immediate)
    val executablePaths by remember { manageGameViewModel.executablePaths }.collectAsState()
    val findingExecutablePathsInProgress by remember { manageGameViewModel.findingExecutablePathsInProgress }
        .collectAsState()
    var checkForUpdates by remember { manageGameViewModel.checkForUpdates }.collectAsMutableState()
    var currentPlayState by remember { manageGameViewModel.currentPlayState }.collectAsMutableState()
    val currentGamesLists by remember { manageGameViewModel.currentGamesLists }.collectAsMutableState()
    var hidden by remember { manageGameViewModel.hidden }.collectAsMutableState()
    val tags by remember { manageGameViewModel.tags }.collectAsState()
    val isF95Game by remember { manageGameViewModel.isF95Game }.collectAsState()
    val saveInProgress by remember { manageGameViewModel.saveInProgress }.collectAsState()
    val titleError by remember { manageGameViewModel.titleError }.collectAsState()
    val releaseDateError by remember { manageGameViewModel.releaseDateError }.collectAsState()
    val firstReleaseDateError by remember { manageGameViewModel.firstReleaseDateError }.collectAsState()
    val playStates by remember { manageGameViewModel.playStates }.collectAsState(emptyList())
    val gamesLists by remember { manageGameViewModel.gamesLists }.collectAsState(emptyList())

    LaunchedEffect(null) {
        manageGameViewModel.gameNotFound.collect {
            onCloseRequest()
        }
    }

    LaunchedEffect(null) {
        manageGameViewModel.onGameSaved.collect {
            onCloseRequest()
            when (mode) {
                ManageGameViewModel.Mode.CreateCustomGame ->
                    manageGameViewModel.showToast(Res.string.editGameToastGameCreated)
                is ManageGameViewModel.Mode.EditGame ->
                    manageGameViewModel.showToast(Res.string.editGameToastGameUpdated)
            }
        }
    }

    val modifier = if (scrollable) {
        Modifier.verticalScroll(rememberScrollState())
    } else {
        Modifier
    }

    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
        ) {
            Section(
                title = stringResource(Res.string.editGameScreenSectionTitleGameDetails),
            ) {
                if (!isF95Game) {
                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp).fillMaxWidth(),
                        value = title,
                        onValueChange = {
                            title = it
                        },
                        label = { Text(stringResource(Res.string.editGameInputLabelTitle)) },
                        isError = titleError,
                    )
                    if (titleError) {
                        Text(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            text = stringResource(Res.string.editGameInputError),
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.error,
                        )
                    }
                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp).fillMaxWidth(),
                        value = imageUrl,
                        onValueChange = {
                            imageUrl = it
                        },
                        label = { Text(stringResource(Res.string.editGameInputLabelImageUrl)) },
                    )
                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp).fillMaxWidth(),
                        value = version,
                        onValueChange = {
                            version = it
                        },
                        label = { Text(stringResource(Res.string.editGameInputLabelVersion)) },
                    )
                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp).fillMaxWidth(),
                        value = releaseDate,
                        onValueChange = {
                            releaseDate = it
                                .filter { it.isDigit() }
                                .take(DateVisualTransformation.CHAR_COUNT_WITHOUT_DIVIDER)
                        },
                        label = { Text(stringResource(Res.string.editGameInputLabelReleaseDate)) },
                        visualTransformation = DateVisualTransformation,
                        placeholder = { Text(DateVisualTransformation.MASK) },
                        isError = releaseDateError,
                    )
                    if (releaseDateError) {
                        Text(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            text = stringResource(Res.string.editGameInputError),
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.error,
                        )
                    }
                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp).fillMaxWidth(),
                        value = firstReleaseDate,
                        onValueChange = {
                            firstReleaseDate = it
                                .filter { it.isDigit() }
                                .take(DateVisualTransformation.CHAR_COUNT_WITHOUT_DIVIDER)
                        },
                        label = { Text(stringResource(Res.string.editGameInputLabelFirstReleaseDate)) },
                        visualTransformation = DateVisualTransformation,
                        placeholder = { Text(DateVisualTransformation.MASK) },
                        isError = firstReleaseDateError,
                    )
                    if (firstReleaseDateError) {
                        Text(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            text = stringResource(Res.string.editGameInputError),
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.error,
                        )
                    }

                    val state = rememberChipTextFieldState<Chip>()
                    LaunchedEffect(state, tags) {
                        if (state.chips.isEmpty() && tags.isNotEmpty()) {
                            state.chips = tags.map(::Chip)
                        }
                    }

                    LaunchedEffect(state) {
                        snapshotFlow { state.chips.map { it.text } }
                            .collect { manageGameViewModel.setTags(it) }
                    }
                    OutlinedChipTextField(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp).fillMaxWidth(),
                        state = state,
                        onSubmit = ::Chip,
                        label = { Text(stringResource(Res.string.editGameInputLabelTags)) },
                    )
                }
                OutlinedTextField(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp).fillMaxWidth(),
                    value = notes.orEmpty(),
                    onValueChange = {
                        notes = it
                    },
                    label = { Text(stringResource(Res.string.editGameInputLabelNotes)) },
                )
            }
            Spacer(
                modifier = Modifier.height(10.dp),
            )
            Section(
                title = stringResource(Res.string.editGameScreenSectionTitleExecutablePaths),
            ) {
                ExecutablePaths(
                    executablePaths = executablePaths,
                    findingExecutablePathsInProgress = findingExecutablePathsInProgress,
                    addExecutablePath = manageGameViewModel::addExecutablePath,
                    deleteExecutablePath = manageGameViewModel::deleteExecutablePath,
                    setExecutablePath = manageGameViewModel::setExecutablePath,
                    findExecutables = manageGameViewModel::findExecutablePaths,
                )
            }
            Spacer(
                modifier = Modifier.height(10.dp),
            )
            Section(
                title = stringResource(Res.string.editGameScreenSectionTitleOptions),
            ) {
                Item(
                    title = stringResource(Res.string.editGameScreenItemTitlePlayState),
                    subtitle = stringResource(Res.string.editGameScreenItemDescriptionPlayState),
                    endContent = {
                        Dropdown(
                            values = playStates,
                            currentValue = currentPlayState,
                            onValueChanged = {
                                currentPlayState = it
                            },
                            valueToString = {
                                it.label
                            },
                        )
                    },
                )
                Divider()
                Item(
                    title = stringResource(Res.string.editGameScreenItemTitleLists),
                    subtitle = stringResource(Res.string.editGameScreenItemDescriptionLists),
                    endContent = {
                        MultiselectDropdown(
                            values = gamesLists,
                            currentValue = currentGamesLists,
                            onCheckedChanged = { value, checked ->
                                manageGameViewModel.updateGameLists(value, checked)
                            },
                            valueToString = {
                                it.name
                            },
                        )
                    },
                )
                Divider()
                if (isF95Game) {
                    Item(
                        title = stringResource(Res.string.editGameScreenItemTitleCheckForUpdates),
                        subtitle = stringResource(Res.string.editGameScreenItemDescriptionCheckForUpdates),
                        endContent = {
                            Toggle(checkForUpdates) {
                                checkForUpdates = it
                            }
                        },
                    )
                    Divider()
                }
                Item(
                    title = stringResource(Res.string.editGameScreenItemTitleArchived),
                    subtitle = stringResource(Res.string.editGameScreenItemDescriptionArchived),
                    endContent = {
                        Toggle(hidden) {
                            hidden = it
                        }
                    },
                )
            }
            Spacer(
                modifier = Modifier.height(10.dp),
            )
            Button(
                enabled = !saveInProgress,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    manageGameViewModel.save()
                },
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(
                    text = stringResource(Res.string.editGameButtonSave),
                )
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
fun ColumnScope.ExecutablePaths(
    executablePaths: List<String>,
    findingExecutablePathsInProgress: Boolean,
    addExecutablePath: (executablePath: String) -> Unit,
    deleteExecutablePath: (index: Int) -> Unit,
    setExecutablePath: (index: Int, executablePath: String) -> Unit,
    findExecutables: () -> Unit,
) {
    if (executablePaths.isEmpty()) {
        NoExecutablePaths()
    }

    ExecutablePathsList(
        executablePaths = executablePaths,
        setExecutablePath = setExecutablePath,
        deleteExecutablePath = {
            deleteExecutablePath(it)
        },
    )
    ExecutablePathsOptions(
        currentPath = executablePaths.lastOrNull(),
        addExecutablePath = addExecutablePath,
        findingExecutablePathsInProgress = findingExecutablePathsInProgress,
        findExecutables = findExecutables,
    )
}

@Composable
private fun ColumnScope.ExecutablePathsOptions(
    currentPath: String?,
    addExecutablePath: (executablePath: String) -> Unit,
    findingExecutablePathsInProgress: Boolean,
    findExecutables: () -> Unit,
) {
    val filePickerLauncher = rememberFilePickerLauncher(
        pickerType = PickerType.File,
        initialDirectory = currentPath,
        extensions = osExecutableExtensions(),
    ) { file ->
        file?.absolutePath?.let {
            addExecutablePath(it)
        }
    }
    Row(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val interactionSourceAdd = remember { MutableInteractionSource() }
        val isHoveredAdd by interactionSourceAdd.collectIsHoveredAsStateDelayed()
        Icon(
            modifier = Modifier.padding(10.dp).clickable {
                filePickerLauncher.launch()
            }.hoverable(interactionSourceAdd),
            imageVector = Icons.Outlined.Add,
            contentDescription = null,
        )
        if (isHoveredAdd && LocalWindowControl.current?.windowFocused?.value == true) {
            HoverExplanation(stringResource(Res.string.hoverExplanationAddExecutable))
        }
        Box(
            modifier = Modifier.padding(10.dp),
        ) {
            if (findingExecutablePathsInProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                )
            } else {
                val interactionSourceSearch = remember { MutableInteractionSource() }
                val isHoveredSearch by interactionSourceSearch.collectIsHoveredAsStateDelayed()
                Icon(
                    modifier = Modifier.clickable {
                        findExecutables()
                    }.hoverable(interactionSourceSearch),
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                )
                if (isHoveredSearch && LocalWindowControl.current?.windowFocused?.value == true) {
                    HoverExplanation(stringResource(Res.string.hoverExplanationSearchExecutable))
                }
            }
        }
    }
}

@Composable
private fun ExecutablePathsList(
    executablePaths: List<String>,
    setExecutablePath: (index: Int, executablePath: String) -> Unit,
    deleteExecutablePath: (index: Int) -> Unit,
) {
    executablePaths.forEachIndexed { index, executablePath ->
        val filePickerLauncher = rememberFilePickerLauncher(
            pickerType = PickerType.File,
            initialDirectory = executablePaths[index],
            extensions = osExecutableExtensions(),
        ) { file ->
            file?.absolutePath?.let {
                setExecutablePath(index, it)
            }
        }
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp).fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier.weight(1f).padding(10.dp).fillMaxWidth(),
                text = executablePath,
            )
            Spacer(
                modifier = Modifier.width(10.dp),
            )
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically).clickable {
                    filePickerLauncher.launch()
                },
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
            )
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically).clickable {
                    deleteExecutablePath(index)
                },
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
            )
        }
        Divider(
            modifier = Modifier.padding(horizontal = 10.dp),
        )
    }
}

@Composable
private fun NoExecutablePaths() {
    val addIconId = "addIcon"
    val searchIconId = "searchIcon"
    Text(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        text = buildAnnotatedString {
            append(stringResource(Res.string.editGameScreenNoExecutablePaths1))
            append(" ")
            appendInlineContent(addIconId, "[$addIconId]")
            append(" ")
            append(stringResource(Res.string.editGameScreenNoExecutablePaths2))
            append(" ")
            appendInlineContent(searchIconId, "[$searchIconId]")
            append(" ")
            append(stringResource(Res.string.editGameScreenNoExecutablePaths3))
        },
        inlineContent = mapOf(
            Pair(
                addIconId,
                InlineTextContent(
                    Placeholder(
                        width = 24.sp,
                        height = 24.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "",
                    )
                },
            ),
            Pair(
                searchIconId,
                InlineTextContent(
                    Placeholder(
                        width = 24.sp,
                        height = 24.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "",
                    )
                },
            ),
        ),
        textAlign = TextAlign.Center,
    )
    Divider(
        modifier = Modifier.padding(horizontal = 10.dp),
    )
}

private fun osExecutableExtensions(): Array<String> {
    return arrayOf(
        when (os) {
            OS.Linux -> "sh"
            OS.Windows -> "exe"
            OS.Mac -> "app"
        },
    )
}
