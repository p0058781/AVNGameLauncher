package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.pickeExecutablePathDialogTitle

private const val EXECUTABLE_PATH_MAX_LENGTH = 50

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
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier,
            ) {
                executablePaths.forEach {
                    Text(
                        modifier = Modifier.padding(10.dp).clickable {
                            onExecutablePicked(it)
                        },
                        text = "...${it.takeLast(EXECUTABLE_PATH_MAX_LENGTH)}",
                    )
                }
            }
        }
    }
}
