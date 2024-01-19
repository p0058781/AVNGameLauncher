package org.skynetsoftware.avnlauncher.domain.utils

import kotlinx.coroutines.flow.MutableStateFlow

@Suppress("FunctionNaming")
fun <T> MutableStateFlow(initialValue: () -> T): MutableStateFlow<T> {
    return MutableStateFlow(initialValue())
}
