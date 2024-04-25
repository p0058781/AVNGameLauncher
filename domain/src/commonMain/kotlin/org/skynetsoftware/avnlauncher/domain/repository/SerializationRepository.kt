package org.skynetsoftware.avnlauncher.domain.repository

import org.skynetsoftware.avnlauncher.domain.model.importexport.ImportExportData

interface SerializationRepository {
    fun serialize(): String

    fun deserialize(): ImportExportData
}