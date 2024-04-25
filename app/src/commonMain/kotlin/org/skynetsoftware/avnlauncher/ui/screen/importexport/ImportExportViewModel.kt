package org.skynetsoftware.avnlauncher.ui.screen.importexport

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository
import org.skynetsoftware.avnlauncher.domain.repository.SerializationRepository

class ImportExportViewModel(
    private val serializationRepository: SerializationRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    val importFile = MutableStateFlow<String?>(null)

    val importGames = MutableStateFlow(true)
    val importSettings = MutableStateFlow(true)
    val exportDir = MutableStateFlow<String?>(null)

    val exportFileName = MutableStateFlow<String>("avn-launcher-export.json")
    val exportGames = MutableStateFlow(true)
    val exportSettings = MutableStateFlow(true)

    fun import() = viewModelScope.launch(dispatcher) {

    }

    fun export() = viewModelScope.launch(dispatcher) {
        val serialized = serializationRepository.serialize()
    }
}