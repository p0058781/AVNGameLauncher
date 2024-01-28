package org.skynetsoftware.avnlauncher.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.data.GameImport
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.utils.Result
import org.skynetsoftware.avnlauncher.logger.Logger
import org.skynetsoftware.avnlauncher.ui.screen.import.FIRST_PLAYED_DATE_FORMAT
import org.skynetsoftware.avnlauncher.ui.screen.import.FIRST_PLAYED_DIVIDER
import org.skynetsoftware.avnlauncher.utils.SimpleDateFormat
import org.skynetsoftware.avnlauncher.utils.hoursToMilliseconds

class ImportGameScreenModel(
    private val gameImport: GameImport,
    private val logger: Logger,
) : ScreenModel {
    val threadId = MutableStateFlow<String?>(null)
    val playTime = MutableStateFlow<String?>(null)
    val firstPlayed = MutableStateFlow<String?>(null)

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> get() = _state

    fun import() {
        screenModelScope.launch {
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
                    val dateFormat = SimpleDateFormat(
                        FIRST_PLAYED_DATE_FORMAT.replace(FIRST_PLAYED_DIVIDER.toString(), ""),
                    )
                    try {
                        dateFormat.parse(firstPlayed)!!
                    } catch (e: Exception) {
                        logger.error(e)
                        _state.emit(State.Error(ValidationFirstPlayedInvalidException()))
                        return@launch
                    }
                }

                _state.emit(State.Importing)
                val threadId = it.toIntOrNull()
                val gameResult = if (threadId == null) {
                    gameImport.importGame(it, playTime, firstPlayed)
                } else {
                    gameImport.importGame(threadId, playTime, firstPlayed)
                }
                when (gameResult) {
                    is Result.Error -> _state.emit(State.Error(gameResult.exception))
                    is Result.Ok -> _state.emit(State.Imported(gameResult.value))
                }
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

    class ValidationPlayTimeInvalidException : Exception()

    class ValidationFirstPlayedInvalidException : Exception()
}
