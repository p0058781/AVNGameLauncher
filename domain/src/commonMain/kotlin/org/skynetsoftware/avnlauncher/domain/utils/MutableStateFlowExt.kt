package org.skynetsoftware.avnlauncher.domain.utils

import kotlinx.coroutines.flow.MutableStateFlow

fun <T> MutableStateFlow(initialValue: () -> T): MutableStateFlow<T> {
    return MutableStateFlow(initialValue())
}
