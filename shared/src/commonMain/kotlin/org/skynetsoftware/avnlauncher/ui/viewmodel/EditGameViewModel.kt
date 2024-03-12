package org.skynetsoftware.avnlauncher.ui.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository

class EditGameViewModel(private val gamesRepository: GamesRepository, private val game: Game) : ViewModel() {
    val title = MutableStateFlow(game.title)
    val imageUrl = MutableStateFlow(game.imageUrl)
    val checkForUpdates = MutableStateFlow(game.checkForUpdates)

    private val _executablePaths = MutableStateFlow(game.executablePaths.addSingleEmptyValueIfEmptySet())
    val executablePaths: StateFlow<List<String>> get() = _executablePaths

    fun save() =
        viewModelScope.launch {
            val executablePath = executablePaths.value.removeEmptyValues()
            val title = title.value
            val imageUrl = imageUrl.value
            val checkForUpdates = checkForUpdates.value

            gamesRepository.updateGame(game.f95ZoneThreadId, title, executablePath, imageUrl, checkForUpdates)
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
