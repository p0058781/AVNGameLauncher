package org.skynetsoftware.avnlauncher.data.f95.model

import kotlinx.serialization.Serializable

@Serializable
data class F95Versions(
    val status: String,
    val msg: Map<Int, String>,
)
