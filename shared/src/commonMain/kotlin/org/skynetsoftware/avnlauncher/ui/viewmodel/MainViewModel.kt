package org.skynetsoftware.avnlauncher.ui.viewmodel

import com.jthemedetecor.OsThemeDetector
import dev.icerock.moko.mvvm.viewmodel.ViewModel
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
    private val themeDetector: OsThemeDetector,
) : ViewModel() {
    val state: StateFlow<State> = stateHandler.state
    val remoteClientMode = configManager.remoteClientMode
    val sfwMode = settingsManager.sfwModeEnabled

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _isDarkTheme = MutableStateFlow<Boolean>(themeDetector.isDark)
    val isDarkTheme: StateFlow<Boolean> get() = _isDarkTheme

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

        themeDetector.registerListener {
            viewModelScope.launch {
                _isDarkTheme.emit(it)
            }
        }
    }

    fun showToast(message: String) {
        eventCenter.emit(Event.ToastMessage(message))
    }

    fun toggleSfwMode() =
        viewModelScope.launch {
            settingsManager.setSfwModeEnabled(!sfwMode.value)
        }
}
