package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.runtime.Composable

@Composable
expect fun GamePicker(
    visible: Boolean,
    currentPath: String?,
    onGamePicked: (game: String?) -> Unit,
)

@Composable
expect fun GamesDirPicker(
    visible: Boolean,
    currentDir: String?,
    onDirPicked: (dir: String?) -> Unit,
)
