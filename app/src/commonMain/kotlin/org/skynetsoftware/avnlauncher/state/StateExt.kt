package org.skynetsoftware.avnlauncher.state

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.stateCheckingForUpdates
import org.skynetsoftware.avnlauncher.app.generated.resources.stateIdle
import org.skynetsoftware.avnlauncher.app.generated.resources.statePlaying

@Composable
fun State.buildText() =
    buildString {
        when (val state = this@buildText) {
            State.Idle -> append(stringResource(Res.string.stateIdle))
            is State.Playing -> append(stringResource(Res.string.statePlaying, state.game.title))
            State.UpdateCheckRunning -> append(stringResource(Res.string.stateCheckingForUpdates))
        }
    }
