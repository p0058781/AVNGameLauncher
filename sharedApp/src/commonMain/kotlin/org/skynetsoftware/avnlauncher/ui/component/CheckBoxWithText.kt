package org.skynetsoftware.avnlauncher.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CheckBoxWithText(
    text: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    description: String? = null,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
            Text(
                text = text,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
        description?.let {
            Text(
                text = description,
                modifier = Modifier,
                style = MaterialTheme.typography.caption,
            )
        }
    }
}
