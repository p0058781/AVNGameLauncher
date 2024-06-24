package org.skynetsoftware.avnlauncher.domain.model

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.playStateLabelCompleted
import org.skynetsoftware.avnlauncher.app.generated.resources.playStateLabelNotStarted
import org.skynetsoftware.avnlauncher.app.generated.resources.playStateLabelPlaying
import org.skynetsoftware.avnlauncher.app.generated.resources.playStateLabelWaitingForUpdate

@Composable
fun PlayState.label(): String =
    stringResource(
        when (this) {
            PlayState.NotStarted -> Res.string.playStateLabelNotStarted
            PlayState.Playing -> Res.string.playStateLabelPlaying
            PlayState.WaitingForUpdate -> Res.string.playStateLabelWaitingForUpdate
            PlayState.Completed -> Res.string.playStateLabelCompleted
        },
    )
