package org.skynetsoftware.avnlauncher.ui.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.resources.Images
import org.skynetsoftware.avnlauncher.ui.component.IconAction
import org.skynetsoftware.avnlauncher.ui.component.TextAction

@Composable
actual fun RowScope.ToolbarTitle(content: @Composable RowScope.() -> Unit) {
    content()
}

@Composable
actual fun RowScope.ToolbarSearch(content: @Composable RowScope.() -> Unit) {
    content()
}

@Composable
actual fun RowScope.ToolbarState(content: @Composable RowScope.() -> Unit) {
    content()
}

@Composable
actual fun ToolbarActions(
    modifier: Modifier,
    sfwMode: Boolean,
    maximized: Boolean,
    startUpdateCheck: () -> Unit,
    onImportGameClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onSfwModeClicked: () -> Unit,
    onToggleMaximizedClicked: () -> Unit,
    exitApplication: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
    ) {
        TextAction(stringResource(if (sfwMode) MR.strings.toolbarActionSfw else MR.strings.toolbarActionNsfw)) {
            onSfwModeClicked()
        }
        IconAction(Images.import) {
            onImportGameClicked()
        }
        IconAction(Images.refresh) {
            startUpdateCheck()
        }
        IconAction(Images.settings) {
            onSettingsClicked()
        }
        IconAction(if (maximized) Images.minimize else Images.maximize) {
            onToggleMaximizedClicked()
        }
        IconAction(Images.close) {
            exitApplication()
        }
    }
}
