package org.skynetsoftware.avnlauncher.mode

sealed class StringValue {
    class StringResource(val stringResource: org.jetbrains.compose.resources.StringResource) : StringValue()

    class String(val string: kotlin.String) : StringValue()
}
