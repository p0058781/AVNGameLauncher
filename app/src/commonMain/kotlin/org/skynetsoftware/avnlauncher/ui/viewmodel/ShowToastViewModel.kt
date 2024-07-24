package org.skynetsoftware.avnlauncher.ui.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.jetbrains.compose.resources.StringResource
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter

abstract class ShowToastViewModel(
    private val eventCenter: EventCenter,
) : ViewModel() {
    fun showToast(message: String) {
        eventCenter.emit(Event.ToastMessage(message))
    }

    fun showToast(message: StringResource) {
        eventCenter.emit(Event.ToastMessage(message))
    }
}
