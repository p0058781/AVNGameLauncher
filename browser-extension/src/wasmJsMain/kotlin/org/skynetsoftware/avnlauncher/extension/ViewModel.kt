package org.skynetsoftware.avnlauncher.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.Res
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.notGameThread
import org.skynetsoftware.avnlauncher.config.Config
import org.skynetsoftware.avnlauncher.extension.model.DisplayableError
import org.skynetsoftware.avnlauncher.extension.model.GameDto
import org.skynetsoftware.avnlauncher.extension.model.Tab
import org.skynetsoftware.avnlauncher.extension.repository.GameRepository
import kotlin.js.Promise

fun activeTab(): Promise<JsArray<Tab>> = js("browser.tabs.query({active: true, currentWindow: true})")

fun printError(error: JsAny): Unit = js("console.error(error)")

class ViewModel(private val gameRepository: GameRepository) {
    enum class Screen {
        Loading,
        Game,
    }

    enum class LoadingScreenState {
        Loading,
        Error,
        ;

        fun toUiState(): UiState {
            return when (this) {
                Loading -> UiState.Loading
                Error -> UiState.Error
            }
        }
    }

    enum class GameScreenState {
        Loading,
        Error,
        GameDetails,
        AddGame,
        ;

        fun toUiState(): UiState {
            return when (this) {
                Loading -> UiState.Loading
                Error -> UiState.Error
                GameDetails -> UiState.GameDetails
                AddGame -> UiState.AddGame
            }
        }
    }

    enum class UiState {
        Loading,
        Error,
        GameDetails,
        AddGame,
    }

    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _game: MutableStateFlow<GameDto?> = MutableStateFlow(null)
    val game: StateFlow<GameDto?> get() = _game

    private val _gameId: MutableStateFlow<Int?> = MutableStateFlow(null)
    val gameId: StateFlow<Int?> get() = _gameId

    private val _error: MutableStateFlow<DisplayableError?> = MutableStateFlow(null)
    val error: StateFlow<DisplayableError?> get() = _error

    private val screen = MutableStateFlow(Screen.Loading)
    private val loadingState = MutableStateFlow(LoadingScreenState.Loading)
    private val gameState = MutableStateFlow(GameScreenState.Loading)

    val uiState: Flow<UiState> =
        combine(screen, loadingState, gameState) { screen, loadingState, gameState ->
            when (screen) {
                Screen.Loading -> loadingState.toUiState()
                Screen.Game -> gameState.toUiState()
            }
        }

    init {
        viewModelScope.launch {
            val gameId = getGameId()
            if (gameId != null) {
                changeScreen(Screen.Game)
                getGame(gameId)
            }
        }
    }

    private suspend fun getGameId(): Int? {
        loadingState.emit(LoadingScreenState.Loading)
        val tabs = activeTab().await<JsArray<Tab>>()
        if (tabs.length <= 0) {
            printError("Unable to get active tab url".toJsString())
            return null
        }
        val gameId = tabs[0]?.url?.let { Config.Defaults.gameThreadUrlRegex.matchEntire(it) }?.groupValues?.getOrNull(1)?.toIntOrNull()
        if (gameId == null) {
            loadingState.emit(LoadingScreenState.Error)
            _error.emit(DisplayableError.ResError(Res.string.notGameThread))
        } else {
            _error.emit(null)
        }
        _gameId.emit(gameId)
        return gameId
    }

    private suspend fun getGame(gameId: Int) {
        gameState.emit(GameScreenState.Loading)
        when (val game = gameRepository.getGame(gameId)) {
            is GameRepository.GetGameResponse.Error -> {
                gameState.emit(GameScreenState.Error)
                _game.emit(null)
            }
            is GameRepository.GetGameResponse.Game -> {
                gameState.emit(GameScreenState.GameDetails)
                _game.emit(game.gameDto)
            }
            GameRepository.GetGameResponse.GameNotFound -> {
                gameState.emit(GameScreenState.AddGame)
                _game.emit(null)
            }
        }
    }

    private suspend fun changeScreen(screen: Screen) {
        val loadingState = loadingState.value
        if (loadingState != LoadingScreenState.Error) {
            this.screen.emit(screen)
        }
    }

    fun addGame() =
        viewModelScope.launch {
            gameState.emit(GameScreenState.Loading)
            val gameId = gameId.value
            if (gameId == null) {
                gameState.emit(GameScreenState.Error)
            } else {
                val success = gameRepository.addGame(gameId)
                if (success) {
                    getGame(gameId)
                } else {
                    gameState.emit(GameScreenState.Error)
                }
            }
        }
}
