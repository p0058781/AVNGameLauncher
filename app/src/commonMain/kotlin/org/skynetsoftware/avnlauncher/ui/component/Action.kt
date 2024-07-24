package org.skynetsoftware.avnlauncher.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.skynetsoftware.avnlauncher.LocalWindowControl
import org.skynetsoftware.avnlauncher.utils.collectIsHoveredAsStateDelayed

@Composable
fun IconAction(
    icon: DrawableResource,
    hoverExplanation: String? = null,
    action: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsStateDelayed()

    Box(
        modifier = Modifier.padding(10.dp)
            .hoverable(interactionSource),
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.clickable {
                action()
            },
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
        )
        if (isHovered && hoverExplanation != null && LocalWindowControl.current?.windowFocused?.value == true) {
            HoverExplanation(hoverExplanation)
        }
    }
}

@Composable
fun TextAction(
    text: String,
    hoverExplanation: String? = null,
    action: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsStateDelayed()
    Box(
        modifier = Modifier.padding(10.dp)
            .hoverable(interactionSource),
    ) {
        Text(
            text = text,
            modifier = Modifier.clickable {
                action()
            },
            color = MaterialTheme.colors.onPrimary,
        )
        if (isHovered && hoverExplanation != null && LocalWindowControl.current?.windowFocused?.value == true) {
            HoverExplanation(hoverExplanation)
        }
    }
}

@Composable
fun DropdownItemAction(
    text: String,
    icon: DrawableResource?,
    action: () -> Unit,
) {
    DropdownMenuItem(
        onClick = action,
    ) {
        if (icon != null) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.clickable {
                    action()
                },
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
            )
        }
        Spacer(
            modifier = Modifier.width(10.dp),
        )
        Text(text)
    }
}
