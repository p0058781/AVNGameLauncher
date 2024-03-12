package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
actual fun Dialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            content()
        },
        onDismissRequest = onDismiss,
        buttons = {},
    )
}
