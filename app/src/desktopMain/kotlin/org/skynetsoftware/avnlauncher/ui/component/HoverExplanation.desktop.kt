package org.skynetsoftware.avnlauncher.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.rememberCursorPositionProvider

@Composable
actual fun HoverExplanation(text: String) {
    Popup(
        popupPositionProvider = rememberCursorPositionProvider(
            offset = DpOffset(
                x = 0.dp,
                y = 30.dp,
            ),
        ),
    ) {
        Text(
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .border(1.dp, MaterialTheme.colors.onSurface, RoundedCornerShape(5.dp))
                .padding(5.dp),
            text = text,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface,
        )
    }
}
