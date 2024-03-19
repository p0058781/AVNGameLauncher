package org.skynetsoftware.avnlauncher.ui.screen.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.appName
import org.skynetsoftware.avnlauncher.app.generated.resources.totalPlayTime
import org.skynetsoftware.avnlauncher.utils.formatPlayTime

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ToolbarTitle(
    totalPlayTime: Long,
    averagePlayTime: Float,
) {
    Column(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
    ) {
        Text(
            text = stringResource(Res.string.appName),
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onPrimary,
        )
        Text(
            text = stringResource(
                Res.string.totalPlayTime,
            ).format(formatPlayTime(totalPlayTime), averagePlayTime),
        )
    }
}
