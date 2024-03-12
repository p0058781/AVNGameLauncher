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

class MainScreenModel(
    private val eventCenter: EventCenter,
    stateHandler: StateHandler,
    private val settingsRepository: SettingsRepository,
) : ScreenModel {
    val state: StateFlow<State> = stateHandler.state
    val sfwMode = settingsRepository.sfwModeEnabled
    val forceDarkTheme = settingsRepository.forceDarkTheme

    private val _toastMessage = MutableStateFlow<Event.ToastMessage<*>?>(null)
    val toastMessage: StateFlow<Event.ToastMessage<*>?> get() = _toastMessage

    init {
        screenModelScope.launch {
            eventCenter.events.collect {
                if (it is Event.ToastMessage<*>) {
                    _toastMessage.emit(it)
                    delay(it.duration)
                    _toastMessage.emit(null)
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
}
