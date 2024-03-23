package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState

@Composable
actual fun Dialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    DialogWindow(
        state = rememberDialogState(size = DpSize.Unspecified),
        onCloseRequest = onDismiss,
        title = title,
        resizable = false,
    ) {
        content()
    }
}
