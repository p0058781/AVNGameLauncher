package org.skynetsoftware.avnlauncher.ui.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.state.State
import org.skynetsoftware.avnlauncher.state.StateHandler

class MainViewModel(
    private val eventCenter: EventCenter,
    stateHandler: StateHandler,
    configManager: ConfigManager,
) : ViewModel() {
    val state: StateFlow<State> = stateHandler.state
    val remoteClientMode = configManager.remoteClientMode

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    init {
        viewModelScope.launch {
            eventCenter.events.collect {
                if (it is Event.ToastMessage) {
                    _toastMessage.emit(it.message)
                    delay(it.duration)
                    _toastMessage.emit(null)
                }
            }
        }
    }

    fun showToast(message: String) {
        eventCenter.emit(Event.ToastMessage(message))
    }
}
