package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun RowScope.ToolbarTitle(content: @Composable RowScope.() -> Unit)

@Composable
expect fun RowScope.ToolbarSearch(content: @Composable RowScope.() -> Unit)

@Composable
expect fun RowScope.ToolbarState(content: @Composable RowScope.() -> Unit)

@Composable
expect fun ToolbarActions(
    modifier: Modifier = Modifier,
    sfwMode: Boolean,
    startUpdateCheck: () -> Unit,
    onImportGameClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onSfwModeClicked: () -> Unit,
    exitApplication: () -> Unit,
)
