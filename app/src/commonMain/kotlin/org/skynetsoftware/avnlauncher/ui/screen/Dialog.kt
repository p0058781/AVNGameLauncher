package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.runtime.Composable

@Composable
expect fun Dialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
)
