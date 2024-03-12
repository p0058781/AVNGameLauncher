package org.skynetsoftware.avnlauncher.domain.utils

sealed class Result<T> {
    class Ok<T>(val value: T) : Result<T>()

    class Error<T>(val exception: Throwable) : Result<T>()
}

fun <T> Result<T>.valueOrNull(): T? {
    return when (this) {
        is Result.Error -> null
        is Result.Ok -> value
    }
}
