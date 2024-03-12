package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun Dialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface {
            Column {
                Text(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                    text = title,
                    style = MaterialTheme.typography.h6,
                )
                content()
            }
        }
    }
}

/**
 * Apply platform specific windows size modifier
 * On android this will be nothing, on desktop this will be fillMaxHeight
 * This is necessary because the way Dialog works on each platform
 * */
actual fun Modifier.applyPlatformSpecificHeight(): Modifier {
    // don't do anything on android, we want "wrap_content" like behavior, which is default
    return this
}
