package org.skynetsoftware.avnlauncher.ui.screen.customstatuses

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.playStateErrorAlreadyExists
import org.skynetsoftware.avnlauncher.domain.coroutines.CoroutineDispatchers
import org.skynetsoftware.avnlauncher.domain.model.PlayState
import org.skynetsoftware.avnlauncher.domain.repository.PlayStateRepository
import java.util.UUID

class CustomStatusesViewModel(
    private val playStateRepository: PlayStateRepository,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val unsavedPlayStates = MutableStateFlow(emptyList<PlayStateViewItem>())

    private val savedPlayStates = playStateRepository.playStates

    val playStates =
        combine(savedPlayStates, unsavedPlayStates) { savedPlayStates, unsavedPlayStates ->
            ArrayList<PlayStateViewItem>().apply {
                addAll(
                    savedPlayStates.map { savedPlayState ->
                        unsavedPlayStates.find { unsavedPlayState -> unsavedPlayState.id == savedPlayState.id }
                            ?: PlayStateViewItem(
                                id = savedPlayState.id,
                                label = savedPlayState.label,
                                description = savedPlayState.description,
                                editing = false,
                                error = null,
                            )
                    },
                )
                addAll(unsavedPlayStates.filter { it.id.isBlank() })
            }
        }

    fun edit(playStateViewItem: PlayStateViewItem) =
        viewModelScope.launch {
            if (playStateViewItem.id.isBlank()) {
                return@launch
            }
            val unsavedPlayStates = unsavedPlayStates.value.toMutableList()
            unsavedPlayStates.removeAll { it.id == playStateViewItem.id }
            unsavedPlayStates.add(playStateViewItem.copy(editing = true))
            this@CustomStatusesViewModel.unsavedPlayStates.emit(unsavedPlayStates)
        }

    fun delete(playStateViewItem: PlayStateViewItem) =
        viewModelScope.launch(coroutineDispatchers.io) {
            if (playStateViewItem.id.isBlank()) {
                return@launch
            }
            playStateRepository.delete(playStateViewItem.id)
        }

    fun save(
        playStateViewItem: PlayStateViewItem,
        label: String,
        description: String?,
    ) = viewModelScope.launch(coroutineDispatchers.io) {
        if (playStateViewItem.id.isBlank()) {
            val existingPlayState = playStateRepository.getByLabel(label)
            if (existingPlayState != null) {
                setError(playStateViewItem, Res.string.playStateErrorAlreadyExists)
            } else {
                playStateRepository.insert(
                    PlayState(
                        id = UUID.randomUUID().toString(),
                        label = label,
                        description = description,
                    ),
                )
                val unsavedPlayStates = unsavedPlayStates.value.toMutableList()
                unsavedPlayStates.removeIf { it == playStateViewItem }
                this@CustomStatusesViewModel.unsavedPlayStates.emit(unsavedPlayStates)
            }
        } else {
            playStateRepository.update(
                playStateViewItem.id,
                label = label,
                description = description,
            )
            val unsavedPlayStates = unsavedPlayStates.value.toMutableList()
            unsavedPlayStates.removeIf { it == playStateViewItem }
            this@CustomStatusesViewModel.unsavedPlayStates.emit(unsavedPlayStates)
        }
    }

    fun add() =
        viewModelScope.launch {
            val unsavedPlayStates = unsavedPlayStates.value.toMutableList()
            unsavedPlayStates.add(
                PlayStateViewItem(
                    id = "",
                    label = "",
                    description = null,
                    editing = true,
                    error = null,
                ),
            )
            this@CustomStatusesViewModel.unsavedPlayStates.emit(unsavedPlayStates)
        }

    private fun setError(
        playStateViewItem: PlayStateViewItem,
        error: StringResource?,
    ) = viewModelScope.launch {
        val unsavedPlayStates = unsavedPlayStates.value.toMutableList()
        val index = unsavedPlayStates.indexOfFirst { it == playStateViewItem }
        if (index >= 0) {
            val newPlayState = playStateViewItem.copy(error = error)
            unsavedPlayStates[index] = newPlayState
        }
        this@CustomStatusesViewModel.unsavedPlayStates.emit(unsavedPlayStates)
    }
}
