package org.skynetsoftware.avnlauncher.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun IconAction(
    icon: DrawableResource,
    action: () -> Unit,
) {
    Box(
        modifier = Modifier.padding(10.dp),
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.clickable {
                action()
            },
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
        )
    }
}

@Composable
fun TextAction(
    text: String,
    action: () -> Unit,
) {
    Box(
        modifier = Modifier.padding(10.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.clickable {
                action()
            },
            color = MaterialTheme.colors.onPrimary,
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
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
