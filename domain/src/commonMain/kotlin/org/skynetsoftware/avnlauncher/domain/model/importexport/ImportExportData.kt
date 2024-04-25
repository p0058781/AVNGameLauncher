package org.skynetsoftware.avnlauncher.domain.model.importexport

import kotlinx.serialization.Serializable

@Serializable
data class ImportExportData(
    val games: List<SerializedGame>,
    val settings: SerializedSettings
)