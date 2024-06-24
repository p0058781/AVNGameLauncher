package org.skynetsoftware.avnlauncher.extension.model

import org.jetbrains.compose.resources.StringResource

sealed class DisplayableError {
    class StringError(val error: String) : DisplayableError()

    class ResError(val error: StringResource) : DisplayableError()
}
