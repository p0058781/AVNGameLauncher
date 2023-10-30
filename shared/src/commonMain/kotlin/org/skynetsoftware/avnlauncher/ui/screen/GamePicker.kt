package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.runtime.Composable

@Composable
expect fun GamePicker(visible: Boolean, onGamePicked: (game: String?) -> Unit)
