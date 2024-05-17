package org.skynetsoftware.avnlauncher.ui.screen.editgame

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlayState
import org.skynetsoftware.avnlauncher.domain.model.isF95Game
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.ui.input.DateVisualTransformation
import org.skynetsoftware.avnlauncher.ui.viewmodel.ShowToastViewModel
import org.skynetsoftware.avnlauncher.utils.ExecutableFinder
import org.skynetsoftware.avnlauncher.utils.isValidDateTimeFormat
import java.text.SimpleDateFormat
import kotlin.random.Random

class ManageGameViewModel(
    private val mode: Mode,
    private val gamesRepository: GamesRepository,
    private val executableFinder: ExecutableFinder,
    eventCenter: EventCenter,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ShowToastViewModel(eventCenter) {
    val title = MutableStateFlow("")
    val imageUrl = MutableStateFlow("")
    val checkForUpdates = MutableStateFlow(true)
    val currentPlayState = MutableStateFlow(PlayState.Playing)
    val hidden = MutableStateFlow(false)
    val notes = MutableStateFlow<String?>(null)
    val version = MutableStateFlow("")
    val releaseDate = MutableStateFlow("")
    val firstReleaseDate = MutableStateFlow("")

    private val _executablePaths = MutableStateFlow(emptyList<String>())
    val executablePaths: StateFlow<List<String>> get() = _executablePaths

    private val _tags = MutableStateFlow(emptyList<String>())
    val tags: StateFlow<List<String>> get() = _tags

    private val _findingExecutablePathsInProgress = MutableStateFlow(false)
    val findingExecutablePathsInProgress: StateFlow<Boolean> get() = _findingExecutablePathsInProgress

    private val _saveInProgress = MutableStateFlow(false)
    val saveInProgress: StateFlow<Boolean> get() = _saveInProgress

    private val _gameNotFound = MutableSharedFlow<Unit>(replay = 0)
    val gameNotFound: SharedFlow<Unit> get() = _gameNotFound

    private val _isF95Game = MutableStateFlow(false)
    val isF95Game: StateFlow<Boolean> get() = _isF95Game

    private val _onGameSaved = MutableSharedFlow<Unit>(replay = 0)
    val onGameSaved: SharedFlow<Unit> get() = _onGameSaved

    private val _titleError = MutableStateFlow(false)
    val titleError: StateFlow<Boolean> get() = _titleError

    private val _releaseDateError = MutableStateFlow(false)
    val releaseDateError: StateFlow<Boolean> get() = _releaseDateError

    private val _firstReleaseDateError = MutableStateFlow(false)
    val firstReleaseDateError: StateFlow<Boolean> get() = _firstReleaseDateError

    init {
        if (mode is Mode.EditGame) {
            viewModelScope.launch {
                val game = gamesRepository.get(mode.gameId)
                if (game == null) {
                    _gameNotFound.emit(Unit)
                } else {
                    _isF95Game.emit(game.isF95Game())
                    title.emit(game.title)
                    imageUrl.emit(game.imageUrl)
                    checkForUpdates.emit(game.checkForUpdates)
                    currentPlayState.emit(game.playState)
                    hidden.emit(game.hidden)
                    notes.emit(game.notes)
                    version.emit(game.version)
                    _executablePaths.emit(game.executablePaths.toList())

                    val dateFormat = DateVisualTransformation.getUnmaskedDateFormat()
                    releaseDate.emit(dateFormat.format(game.releaseDate))
                    firstReleaseDate.emit(dateFormat.format(game.firstReleaseDate))
                    _tags.emit(game.tags.toList())
                }
            }
        }
    }

    fun save() =
        viewModelScope.launch {
            _titleError.emit(false)
            _releaseDateError.emit(false)
            _firstReleaseDateError.emit(false)
            _saveInProgress.emit(true)

            val saved = when (mode) {
                Mode.CreateCustomGame -> {
                    createCustomGame()
                }
                is Mode.EditGame -> {
                    updateGame(mode.gameId)
                }
            }

            if (saved) {
                _onGameSaved.emit(Unit)
            }
            _saveInProgress.emit(false)
        }

    private suspend fun updateGame(gameId: Int): Boolean {
        val executablePath = executablePaths.value.removeEmptyValues()
        val checkForUpdates = checkForUpdates.value
        val playState = currentPlayState.value
        val hidden = hidden.value
        val notes = notes.value
        val dateFormat = DateVisualTransformation.getUnmaskedDateFormat()
        val title = title.value
        val imageUrl = imageUrl.value
        val version = version.value
        val releaseDate = releaseDate.value
        val firstReleaseDate = firstReleaseDate.value
        val tags = tags.value
        return if (isF95Game.value) {
            gamesRepository.updateGame(
                gameId,
                executablePath,
                checkForUpdates,
                playState,
                hidden,
                notes,
            )
            true
        } else {
            val inputValid = validateInput(title, releaseDate, firstReleaseDate, dateFormat)
            if (inputValid) {
                gamesRepository.updateGame(
                    gameId,
                    title,
                    imageUrl,
                    version,
                    dateFormat.parse(releaseDate)!!.time,
                    dateFormat.parse(firstReleaseDate)!!.time,
                    tags.toSet(),
                    executablePath,
                    checkForUpdates,
                    playState,
                    hidden,
                    notes,
                )
            }
            inputValid
        }
    }

    private suspend fun createCustomGame(): Boolean {
        val executablePath = executablePaths.value.removeEmptyValues()
        val checkForUpdates = checkForUpdates.value
        val playState = currentPlayState.value
        val hidden = hidden.value
        val notes = notes.value
        val dateFormat = DateVisualTransformation.getUnmaskedDateFormat()
        val title = title.value
        val imageUrl = imageUrl.value
        val version = version.value
        val releaseDate = releaseDate.value
        val firstReleaseDate = firstReleaseDate.value
        val tags = tags.value
        val inputValid = validateInput(title, releaseDate, firstReleaseDate, dateFormat)
        if (inputValid) {
            val id = Random.nextInt(Int.MIN_VALUE, 0)
            val game = Game(
                title = title,
                imageUrl = imageUrl,
                f95ZoneThreadId = id,
                executablePaths = executablePath,
                version = version,
                totalPlayTime = 0L,
                rating = 0,
                f95Rating = 0f,
                updateAvailable = false,
                added = Clock.System.now().toEpochMilliseconds(),
                lastPlayedTime = 0L,
                hidden = hidden,
                releaseDate = dateFormat.parse(releaseDate)!!.time,
                firstReleaseDate = dateFormat.parse(firstReleaseDate)!!.time,
                playState = playState,
                availableVersion = null,
                tags = tags.toSet(),
                checkForUpdates = checkForUpdates,
                firstPlayedTime = 0L,
                notes = notes,
                favorite = false,
                playSessions = emptyList(),
            )
            gamesRepository.insertGame(game)
        }
        return inputValid
    }

    private suspend fun validateInput(
        title: String,
        releaseDate: String,
        firstReleaseDate: String,
        dateFormat: SimpleDateFormat,
    ): Boolean {
        return if (title.isBlank()) {
            _titleError.emit(true)
            false
        } else if (!releaseDate.isValidDateTimeFormat(dateFormat)) {
            _releaseDateError.emit(true)
            false
        } else if (!firstReleaseDate.isValidDateTimeFormat(dateFormat)) {
            _firstReleaseDateError.emit(true)
            false
        } else {
            true
        }
    }

    fun setExecutablePath(
        index: Int,
        executablePath: String,
    ) = viewModelScope.launch {
        val executablePaths = executablePaths.value.toMutableList()
        executablePaths[index] = executablePath
        _executablePaths.emit(executablePaths)
    }

    fun deleteExecutablePath(index: Int) =
        viewModelScope.launch {
            val executablePaths = executablePaths.value.toMutableList()
            executablePaths.removeAt(index)
            _executablePaths.emit(executablePaths)
        }

    fun addExecutablePath(executablePath: String) =
        viewModelScope.launch {
            val executablePaths = executablePaths.value.toMutableList()
            executablePaths.add(executablePath)
            _executablePaths.emit(executablePaths)
        }

    fun findExecutablePaths() =
        viewModelScope.launch(ioDispatcher) {
            _findingExecutablePathsInProgress.emit(true)
            val currentExecutables = executablePaths.value.toMutableSet()
            val foundExecutables = executableFinder.findExecutables(title.value)
            currentExecutables.addAll(foundExecutables)
            _executablePaths.emit(currentExecutables.toList())
            _findingExecutablePathsInProgress.emit(false)
        }

    fun setTags(tags: List<String>) =
        viewModelScope.launch {
            _tags.emit(tags)
        }

    sealed class Mode {
        object CreateCustomGame : Mode()

        class EditGame(val gameId: Int) : Mode()
    }
}

private fun List<String>.removeEmptyValues(): Set<String> {
    return this.filter { it.isNotBlank() }.toSet()
}
