package org.skynetsoftware.avnlauncher.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.data.GameImport
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.utils.Result

class ImportGameScreenModel(
    private val gameImport: GameImport,
) : ScreenModel {
    val threadId = MutableStateFlow<String?>(null)

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> get() = _state

    fun import() =
        screenModelScope.launch {
            threadId.value?.let {
                _state.emit(State.Importing)
                when (val gameResult = gameImport.importGame(it.toInt())) {
                    is Result.Error -> _state.emit(State.Error(gameResult.exception))
                    is Result.Ok -> _state.emit(State.Imported(gameResult.value))
                }
            }
        }

    fun resetState() =
        screenModelScope.launch {
            _state.emit(State.Idle)
        }

    sealed class State {
        object Idle : State()

        object Importing : State()

        class Imported(val game: Game) : State()

        class Error(val error: Throwable) : State()
    }
}
