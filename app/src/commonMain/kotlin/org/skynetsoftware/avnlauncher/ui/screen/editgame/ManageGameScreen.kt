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
import androidx.compose.runtime.mutableStateOf
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
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
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
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenItemDescriptionPlayState
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenItemTitleArchived
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameScreenItemTitleCheckForUpdates
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
import org.skynetsoftware.avnlauncher.domain.model.PlayState
import org.skynetsoftware.avnlauncher.logger.Logger
import org.skynetsoftware.avnlauncher.ui.component.Dropdown
import org.skynetsoftware.avnlauncher.ui.component.HoverExplanation
import org.skynetsoftware.avnlauncher.ui.component.Item
import org.skynetsoftware.avnlauncher.ui.component.Section
import org.skynetsoftware.avnlauncher.ui.component.Toggle
import org.skynetsoftware.avnlauncher.ui.input.DateVisualTransformation
import org.skynetsoftware.avnlauncher.ui.screen.GamePicker
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
        onCloseRequest = onCloseRequest,
    )
}

@Composable
fun CreateCustomGameScreen(onCloseRequest: () -> Unit) {
    ManageGameScreen(
        mode = ManageGameViewModel.Mode.CreateCustomGame,
        onCloseRequest = onCloseRequest,
    )
}

@OptIn(ExperimentalResourceApi::class)
@Suppress("LongMethod")
@Composable
private fun ManageGameScreen(
    mode: ManageGameViewModel.Mode,
    manageGameViewModel: ManageGameViewModel = viewModel { parametersOf(mode) },
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
    var hidden by remember { manageGameViewModel.hidden }.collectAsMutableState()
    val tags by remember { manageGameViewModel.tags }.collectAsState()
    val isF95Game by remember { manageGameViewModel.isF95Game }.collectAsState()
    val saveInProgress by remember { manageGameViewModel.saveInProgress }.collectAsState()
    val titleError by remember { manageGameViewModel.titleError }.collectAsState()
    val releaseDateError by remember { manageGameViewModel.releaseDateError }.collectAsState()
    val firstReleaseDateError by remember { manageGameViewModel.firstReleaseDateError }.collectAsState()

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

    Surface(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
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
                            values = PlayState.entries,
                            currentValue = currentPlayState,
                            onValueChanged = {
                                currentPlayState = it
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

@OptIn(ExperimentalResourceApi::class)
@Suppress("LongMethod")
@Composable
fun ColumnScope.ExecutablePaths(
    executablePaths: List<String>,
    findingExecutablePathsInProgress: Boolean,
    addExecutablePath: (executablePath: String) -> Unit,
    deleteExecutablePath: (index: Int) -> Unit,
    setExecutablePath: (index: Int, executablePath: String) -> Unit,
    findExecutables: () -> Unit,
    logger: Logger = koinInject(),
) {
    var showFilePicker by remember { mutableStateOf<ShowFilePicker?>(null) }

    if (executablePaths.isEmpty()) {
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

    executablePaths.forEachIndexed { index, executablePath ->
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
                    showFilePicker = ShowFilePicker.ChangePath(index)
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
    Row(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val interactionSourceAdd = remember { MutableInteractionSource() }
        val isHoveredAdd by interactionSourceAdd.collectIsHoveredAsStateDelayed()
        Icon(
            modifier = Modifier.padding(10.dp).clickable {
                showFilePicker = ShowFilePicker.AddPath
            }.hoverable(interactionSourceAdd),
            imageVector = Icons.Outlined.Add,
            contentDescription = null,
        )
        if (isHoveredAdd) {
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
                if (isHoveredSearch) {
                    HoverExplanation(stringResource(Res.string.hoverExplanationSearchExecutable))
                }
            }
        }
    }

    val currentPath = when (val localShowFilePicker = showFilePicker) {
        is ShowFilePicker.ChangePath -> {
            executablePaths[localShowFilePicker.index]
        }

        ShowFilePicker.AddPath -> executablePaths.lastOrNull() ?: ""
        null -> ""
    }
    GamePicker(showFilePicker != null, currentPath) {
        it?.let {
            when (val localShowFilePicker = showFilePicker) {
                ShowFilePicker.AddPath -> {
                    addExecutablePath(it)
                }

                is ShowFilePicker.ChangePath -> {
                    setExecutablePath(localShowFilePicker.index, it)
                }

                null -> logger.warning("showFilePicker null in GamePicker callback")
            }
        }
        showFilePicker = null
    }
}

sealed class ShowFilePicker {
    object AddPath : ShowFilePicker()

    class ChangePath(val index: Int) : ShowFilePicker()
}

@OptIn(ExperimentalResourceApi::class)
expect val inputLabelExecutablePath: StringResource
