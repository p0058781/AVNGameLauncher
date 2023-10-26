package org.skynetsoftware.avnlauncher.data.model

import kotlinx.serialization.Serializable
import org.skynetsoftware.avnlauncher.utils.FileSerializer
import java.io.File

@Serializable
data class Config(
    @Serializable(with = FileSerializer::class)
    val databaseFile: File,
    @Serializable(with = FileSerializer::class)
    val cacheDir: File,
    @Serializable(with = FileSerializer::class)
    val gamesDir: File
)