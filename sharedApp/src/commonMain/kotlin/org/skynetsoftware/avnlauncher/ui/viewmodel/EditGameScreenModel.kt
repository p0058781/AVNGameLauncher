package org.skynetsoftware.avnlauncher.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository
import org.skynetsoftware.avnlauncher.utils.ExecutableFinder

class EditGameScreenModel(
    private val gamesRepository: GamesRepository,
    private val game: Game,
    private val executableFinder: ExecutableFinder,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ScreenModel {
    val title = MutableStateFlow(game.title)
    val imageUrl = MutableStateFlow(game.customImageUrl ?: game.imageUrl)
    val checkForUpdates = MutableStateFlow(game.checkForUpdates)
    val currentPlayState = MutableStateFlow(game.playState)
    val hidden = MutableStateFlow(game.hidden)
    val notes = MutableStateFlow(game.notes)

    private val _executablePaths = MutableStateFlow(game.executablePaths.addSingleEmptyValueIfEmptySet())
    val executablePaths: StateFlow<List<String>> get() = _executablePaths

    private val _findingExecutablePathsInProgress = MutableStateFlow(false)
    val findingExecutablePathsInProgress: StateFlow<Boolean> get() = _findingExecutablePathsInProgress

    fun save() =
        screenModelScope.launch {
            val executablePath = executablePaths.value.removeEmptyValues()
            val title = title.value
            val customImageUrl = imageUrl.value
            val checkForUpdates = checkForUpdates.value
            val playState = currentPlayState.value
            val hidden = hidden.value
            val notes = notes.value

            gamesRepository.updateGame(
                game.f95ZoneThreadId,
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
    ) = screenModelScope.launch {
        val executablePaths = executablePaths.value.toMutableList()
        executablePaths[index] = executablePath
        _executablePaths.emit(executablePaths)
    }

    fun deleteExecutablePath(index: Int) =
        screenModelScope.launch {
            val executablePaths = executablePaths.value.toMutableList()
            executablePaths.removeAt(index)
            _executablePaths.emit(executablePaths)
        }

    fun addExecutablePath() =
        screenModelScope.launch {
            val executablePaths = executablePaths.value.toMutableList()
            executablePaths.add("")
            _executablePaths.emit(executablePaths)
        }

    fun findExecutablePaths() =
        screenModelScope.launch(ioDispatcher) {
            _findingExecutablePathsInProgress.emit(true)
            val currentExecutables = executablePaths.value.toMutableSet()
            val foundExecutables = executableFinder.findExecutables(game.title)
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
