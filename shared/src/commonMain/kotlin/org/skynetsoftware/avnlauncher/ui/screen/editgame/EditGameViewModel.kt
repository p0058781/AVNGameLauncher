package org.skynetsoftware.avnlauncher.ui.screen.editgame

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository

class EditGameViewModel(private val gamesRepository: GamesRepository, private val game: Game) : ViewModel() {
    val title = MutableStateFlow(game.title)
    val imageUrl = MutableStateFlow(game.imageUrl)
    val executablePath = MutableStateFlow(game.executablePath ?: "")

    fun save() = viewModelScope.launch {
        val executablePath = executablePath.value
        val title = title.value
        val imageUrl = imageUrl.value

        if (executablePath != game.executablePath) {
            gamesRepository.updateExecutablePath(executablePath, game)
        }
        if (title != game.title) {
            gamesRepository.updateTitle(title, game)
        }
        if (imageUrl != game.imageUrl) {
            gamesRepository.updateImageUrl(imageUrl, game)
        }
    }
}