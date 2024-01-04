package org.skynetsoftware.avnlauncher.ui.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.settings.SettingsManager
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.state.State
import org.skynetsoftware.avnlauncher.state.StateHandler

class MainViewModel(
    private val eventCenter: EventCenter,
    stateHandler: StateHandler,
    configManager: ConfigManager,
    private val settingsManager: SettingsManager,
) : ViewModel() {
    val state: StateFlow<State> = stateHandler.state
    val remoteClientMode = configManager.remoteClientMode
    val sfwMode = settingsManager.sfwModeEnabled
    val forceDarkTheme = settingsManager.forceDarkTheme

    private val _toastMessage = MutableStateFlow<Event.ToastMessage<*>?>(null)
    val toastMessage: StateFlow<Event.ToastMessage<*>?> get() = _toastMessage

    init {
        viewModelScope.launch {
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
        viewModelScope.launch {
            settingsManager.setSfwModeEnabled(!sfwMode.value)
        }
}
