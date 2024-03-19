package org.skynetsoftware.avnlauncher.ui.screen.editgame

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.domain.model.PlayState
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.ui.viewmodel.ShowToastViewModel
import org.skynetsoftware.avnlauncher.utils.ExecutableFinder

class EditGameViewModel(
    private val gameId: Int,
    private val gamesRepository: GamesRepository,
    private val executableFinder: ExecutableFinder,
    eventCenter: EventCenter,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ShowToastViewModel(eventCenter) {
    val title = MutableStateFlow("")
    val imageUrl = MutableStateFlow("")
    val checkForUpdates = MutableStateFlow(false)
    val currentPlayState = MutableStateFlow(PlayState.Playing)
    val hidden = MutableStateFlow(false)
    val notes = MutableStateFlow<String?>(null)

    private val _executablePaths = MutableStateFlow(emptyList<String>())
    val executablePaths: StateFlow<List<String>> get() = _executablePaths

    private val _findingExecutablePathsInProgress = MutableStateFlow(false)
    val findingExecutablePathsInProgress: StateFlow<Boolean> get() = _findingExecutablePathsInProgress

    private val _gameNotFound = MutableSharedFlow<Unit>(replay = 0)
    val gameNotFound: SharedFlow<Unit> get() = _gameNotFound

    init {
        viewModelScope.launch {
            val game = gamesRepository.get(gameId)
            if (game == null) {
                _gameNotFound.emit(Unit)
            } else {
                title.emit(game.title)
                imageUrl.emit(game.customImageUrl ?: game.imageUrl)
                checkForUpdates.emit(game.checkForUpdates)
                currentPlayState.emit(game.playState)
                hidden.emit(game.hidden)
                notes.emit(game.notes)
                _executablePaths.emit(game.executablePaths.addSingleEmptyValueIfEmptySet())
            }
        }
    }

    fun save() =
        viewModelScope.launch {
            val executablePath = executablePaths.value.removeEmptyValues()
            val title = title.value
            val customImageUrl = imageUrl.value
            val checkForUpdates = checkForUpdates.value
            val playState = currentPlayState.value
            val hidden = hidden.value
            val notes = notes.value

            gamesRepository.updateGame(
                gameId,
                title,
                executablePath,
                customImageUrl,
                checkForUpdates,
                playState,
                hidden,
                notes,
            )
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

    fun addExecutablePath() =
        viewModelScope.launch {
            val executablePaths = executablePaths.value.toMutableList()
            executablePaths.add("")
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
}

private fun Set<String>.addSingleEmptyValueIfEmptySet(): List<String> {
    return if (this.isEmpty()) {
        listOf("")
    } else {
        this.toList()
    }
}

private fun List<String>.removeEmptyValues(): Set<String> {
    return this.filter { it.isNotBlank() }.toSet()
}
