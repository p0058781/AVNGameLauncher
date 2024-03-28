package org.skynetsoftware.avnlauncher.utils

import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MutableInteractionSource.collectIsHoveredAsStateDelayed(delay: Long = 1000L): State<Boolean> {
    val isHovered = remember { mutableStateOf(false) }

    LaunchedEffect(this) {
        val hoverInteractions = mutableListOf<HoverInteraction.Enter>()
        var delayJob: Job? = null
        interactions.collect { interaction ->
            when (interaction) {
                is HoverInteraction.Enter -> {
                    delayJob = launch {
                        delay(delay)
                        hoverInteractions.add(interaction)
                        isHovered.value = hoverInteractions.isNotEmpty()
                    }
                }
                is HoverInteraction.Exit -> {
                    delayJob?.cancel()
                    hoverInteractions.remove(interaction.enter)
                }
            }
            isHovered.value = hoverInteractions.isNotEmpty()
        }
    }
    return isHovered
}
