package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

/**
 * Apply platform specific windows size modifier
 * On android this will be nothing, on desktop this will be fillMaxHeight
 * This is necessary because the way Dialog works on each platform
 * */
actual fun Modifier.applyPlatformSpecificHeight(): Modifier {
    return fillMaxHeight()
}
