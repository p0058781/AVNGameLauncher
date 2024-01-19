package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Apply platform specific windows size modifier
 * On android this will be nothing, on desktop this will be fillMaxHeight
 * This is necessary because the way Dialog works on each platform
 * */
expect fun Modifier.applyPlatformSpecificHeight(): Modifier

@Composable
expect fun Dialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
)
