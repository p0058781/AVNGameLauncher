package org.skynetsoftware.avnlauncher.utils

sealed class Option<T> {
    class None<T> : Option<T>()
    class Some<T>(val value: T) : Option<T>()
}