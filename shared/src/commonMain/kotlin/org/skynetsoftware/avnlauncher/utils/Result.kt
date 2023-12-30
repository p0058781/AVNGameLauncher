package org.skynetsoftware.avnlauncher.utils

sealed class Result<T> {
    class Ok<T>(val value: T) : Result<T>()

    class Error<T>(val exception: Throwable) : Result<T>()
}
