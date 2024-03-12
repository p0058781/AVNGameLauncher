package org.skynetsoftware.avnlauncher.domain.model

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import org.skynetsoftware.avnlauncher.MR

@Composable
fun PlayState.label(): String =
    stringResource(
        when (this) {
            PlayState.Playing -> MR.strings.playStateLabelPlaying
            PlayState.WaitingForUpdate -> MR.strings.playStateLabelWaitingForUpdate
            PlayState.Completed -> MR.strings.playStateLabelCompleted
        },
    )
