package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogWindow

@Composable
actual fun Dialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    DialogWindow(
        onCloseRequest = onDismiss,
        title = title,
        resizable = false,
    ) {
        content()
    }
}
