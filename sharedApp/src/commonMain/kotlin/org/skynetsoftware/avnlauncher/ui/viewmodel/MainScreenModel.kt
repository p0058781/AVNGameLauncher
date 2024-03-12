package org.skynetsoftware.avnlauncher.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.state.State
import org.skynetsoftware.avnlauncher.state.StateHandler
import org.skynetsoftware.avnlauncher.updatechecker.UpdateChecker

class MainScreenModel(
    private val eventCenter: EventCenter,
    stateHandler: StateHandler,
    private val settingsRepository: SettingsRepository,
    private val updateChecker: UpdateChecker,
) : ScreenModel {
    val state: StateFlow<State> = stateHandler.state
    val sfwMode = settingsRepository.sfwModeEnabled

    private val _toastMessage = MutableStateFlow<Event.ToastMessage<*>?>(null)
    val toastMessage: StateFlow<Event.ToastMessage<*>?> get() = _toastMessage

    private val _newUpdateAvailableIndicatorVisible = MutableStateFlow(false)
    val newUpdateAvailableIndicatorVisible: StateFlow<Boolean> get() = _newUpdateAvailableIndicatorVisible

    init {
        screenModelScope.launch {
            eventCenter.events.collect {
                when (it) {
                    is Event.ToastMessage<*> -> {
                        _toastMessage.emit(it)
                        delay(it.duration)
                        _toastMessage.emit(null)
                    }
                    is Event.UpdateCheckComplete -> {
                        if (it.updateCheckResult.games.count { game -> game.updateAvailable } > 0) {
                            _newUpdateAvailableIndicatorVisible.emit(true)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun showToast(message: String) {
        eventCenter.emit(Event.ToastMessage(message))
    }

    fun showToast(message: StringResource) {
        eventCenter.emit(Event.ToastMessage(message))
    }

    fun toggleSfwMode() =
        screenModelScope.launch {
            settingsRepository.setSfwModeEnabled(!sfwMode.value)
        }

    fun startUpdateCheck() {
        screenModelScope.launch {
            val updateCheckResult = updateChecker.checkForUpdates(this, true)
            eventCenter.emit(Event.ToastMessage(updateCheckResult))
        }
    }

    fun resetNewUpdateAvailableIndicatorVisible() =
        screenModelScope.launch {
            _newUpdateAvailableIndicatorVisible.emit(false)
        }
}
