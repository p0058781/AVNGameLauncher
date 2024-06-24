package org.skynetsoftware.avnlauncher.ui.screen.import

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.usecase.ImportGameUseCase
import org.skynetsoftware.avnlauncher.domain.utils.Result
import org.skynetsoftware.avnlauncher.logger.Logger
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.ui.input.DateVisualTransformation
import org.skynetsoftware.avnlauncher.ui.viewmodel.ShowToastViewModel
import org.skynetsoftware.avnlauncher.utils.hoursToMilliseconds

class ImportGameViewModel(
    private val importGame: ImportGameUseCase,
    private val logger: Logger,
    eventCenter: EventCenter,
) : ShowToastViewModel(eventCenter) {
    val threadId = MutableStateFlow<String?>(null)
    val playTime = MutableStateFlow<String?>(null)
    val firstPlayed = MutableStateFlow<String?>(null)

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> get() = _state

    fun import() {
        viewModelScope.launch {
            threadId.value?.let {
                val playTime: Long? = playTime.value?.let { playTime ->
                    val playTimeParsed = playTime.toIntOrNull()
                    if (playTimeParsed == null) {
                        _state.emit(State.Error(ValidationPlayTimeInvalidException()))
                        return@launch
                    }
                    playTimeParsed.hoursToMilliseconds()
                }

                val firstPlayed: Long? = firstPlayed.value?.let { firstPlayed ->
                    val dateFormat = DateVisualTransformation.getUnmaskedDateFormat()
                    try {
                        dateFormat.parse(firstPlayed)!!.time
                    } catch (e: Exception) {
                        logger.error(e)
                        _state.emit(State.Error(ValidationFirstPlayedInvalidException()))
                        return@launch
                    }
                }

                _state.emit(State.Importing)
                when (val gameResult = importGame(it, playTime, firstPlayed)) {
                    is Result.Error -> _state.emit(State.Error(gameResult.exception))
                    is Result.Ok -> _state.emit(State.Imported(gameResult.value))
                }
            }
        }
    }

    fun resetState() =
        viewModelScope.launch {
            _state.emit(State.Idle)
        }

    sealed class State {
        object Idle : State()

        object Importing : State()

        class Imported(val game: Game) : State()

        class Error(val error: Throwable) : State()
    }

    class ValidationPlayTimeInvalidException : Exception()

    class ValidationFirstPlayedInvalidException : Exception()
}
