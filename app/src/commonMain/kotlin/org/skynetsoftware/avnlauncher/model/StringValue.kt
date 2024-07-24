package org.skynetsoftware.avnlauncher.model

sealed class StringValue {
    class StringResource(val stringResource: org.jetbrains.compose.resources.StringResource) : StringValue()

    class String(val string: kotlin.String) : StringValue()
}
