package org.skynetsoftware.avnlauncher.utils

import kotlin.enums.enumEntries

/**
 * Return next enum entry, looping around
 * */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Enum<T>> Enum<T>.next(): T {
    val entries = enumEntries<T>()
    val currentIndex = entries.indexOf(this)
    if (currentIndex < 0 || currentIndex == entries.size - 1) {
        return entries.first()
    }
    return entries[currentIndex + 1]
}

/**
 * Return previous enum entry, looping around
 * */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Enum<T>> Enum<T>.previous(): T {
    val entries = enumEntries<T>()
    val currentIndex = entries.indexOf(this)
    if (currentIndex < 0 || currentIndex == 0) {
        return entries.last()
    }
    return entries[currentIndex - 1]
}
