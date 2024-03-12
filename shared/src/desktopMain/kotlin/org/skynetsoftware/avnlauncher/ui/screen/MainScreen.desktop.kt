package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.skynetsoftware.avnlauncher.resources.R
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
    remoteClientMode: Boolean,
    sfwMode: Boolean,
    startUpdateCheck: () -> Unit,
    onImportGameClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onSfwModeClicked: () -> Unit,
    exitApplication: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
    ) {
        TextAction(if (sfwMode) R.strings.toolbarActionSfw else R.strings.toolbarActionNsfw) {
            onSfwModeClicked()
        }
        if (!remoteClientMode) {
            IconAction(R.images.import) {
                onImportGameClicked()
            }
            IconAction(R.images.refresh) {
                startUpdateCheck()
            }
            IconAction(R.images.settings) {
                onSettingsClicked()
            }
        }
        IconAction(R.images.close) {
            exitApplication()
        }
    }
}
