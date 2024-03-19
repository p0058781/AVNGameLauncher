package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.pickeExecutablePathDialogTitle

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PickExecutableDialog(
    executablePaths: Set<String>,
    onCloseRequest: () -> Unit,
    onExecutablePicked: (executablePath: String) -> Unit,
) {
    Dialog(
        title = stringResource(Res.string.pickeExecutablePathDialogTitle),
        onDismiss = onCloseRequest,
    ) {
        Surface(
            modifier = Modifier.fillMaxHeight(),
        ) {
            LazyColumn(
                modifier = Modifier,
            ) {
                items(executablePaths.toList()) {
                    Text(
                        modifier = Modifier.padding(10.dp).clickable {
                            onExecutablePicked(it)
                        },
                        text = it,
                    )
                }
            }
        }
    }
}
