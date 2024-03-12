package org.skynetsoftware.avnlauncher.ui.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.state.State
import org.skynetsoftware.avnlauncher.state.StateHandler

class MainViewModel(
    stateHandler: StateHandler,
    configManager: ConfigManager,
) : ViewModel() {
    val state: StateFlow<State> = stateHandler.state
    val remoteClientMode = configManager.remoteClientMode
}
