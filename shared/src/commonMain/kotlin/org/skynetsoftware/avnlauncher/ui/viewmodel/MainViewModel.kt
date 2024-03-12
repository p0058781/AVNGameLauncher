package org.skynetsoftware.avnlauncher.ui.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.skynetsoftware.avnlauncher.settings.SettingsManager
import org.skynetsoftware.avnlauncher.state.State
import org.skynetsoftware.avnlauncher.state.StateHandler

class MainViewModel(
    stateHandler: StateHandler,
    settingsManager: SettingsManager
) : ViewModel() {

    val state: StateFlow<State> = stateHandler.state
    val remoteClientMode = settingsManager.remoteClientMode
}