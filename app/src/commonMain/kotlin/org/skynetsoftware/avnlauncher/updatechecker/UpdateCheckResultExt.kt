package org.skynetsoftware.avnlauncher.updatechecker

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.updateCheckUpdateAvailable

@OptIn(ExperimentalResourceApi::class)
@Composable
fun UpdateCheckResult.buildToastMessage(): String {
    return stringResource(Res.string.updateCheckUpdateAvailable, updates.size, exceptions.size)
}
