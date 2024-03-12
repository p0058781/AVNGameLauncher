package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.runtime.Composable

@Composable
actual fun Dialog(title: String, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    Dialog(
        title = title,
        onDismiss = onDismiss
    ) {
        content()
    }
}