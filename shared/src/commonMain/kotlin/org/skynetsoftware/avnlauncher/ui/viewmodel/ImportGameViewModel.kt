package org.skynetsoftware.avnlauncher.ui.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.data.GameImport
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.utils.Result

class ImportGameViewModel(
    private val gameImport: GameImport,
) : ViewModel() {
    val threadId = MutableStateFlow<String?>(null)

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> get() = _state

    fun import() =
        viewModelScope.launch {
            threadId.value?.let {
                _state.emit(State.Importing)
                when (val gameResult = gameImport.importGame(it.toInt())) {
                    is Result.Error -> _state.emit(State.Error(gameResult.exception))
                    is Result.Ok -> _state.emit(State.Imported(gameResult.value))
                }
            }
        }

    sealed class State {
        object Idle : State()

        object Importing : State()

        class Imported(val game: Game) : State()

        class Error(val error: Throwable) : State()
    }
}
