package org.skynetsoftware.avnlauncher.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
            color = MaterialTheme.colors.onSurface,
        )
    }
}
