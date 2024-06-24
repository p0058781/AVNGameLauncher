package org.skynetsoftware.avnlauncher.server.dto

import kotlinx.serialization.Serializable

enum class ErrorCode {
    InvalidInput,
    NotFound,
    InternalError,
}

@Serializable
data class ErrorDto(
    val code: ErrorCode,
    val message: String,
)

fun InvalidInput(message: String) = ErrorDto(ErrorCode.InvalidInput, message)

fun NotFound(message: String) = ErrorDto(ErrorCode.NotFound, message)

fun Internal(message: String) = ErrorDto(ErrorCode.InternalError, message)
