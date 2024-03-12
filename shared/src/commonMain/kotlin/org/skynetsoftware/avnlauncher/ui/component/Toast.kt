package org.skynetsoftware.avnlauncher.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.skynetsoftware.avnlauncher.ui.theme.Foreground
import org.skynetsoftware.avnlauncher.ui.theme.ToastBackground

enum class ToastDuration(val value: Int) {
    Short(1000),
    Long(3000),
}

@Composable
fun Toast(
    text: String,
    onDismiss: () -> Unit,
    duration: ToastDuration = ToastDuration.Long,
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(bottom = 20.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Surface(
            modifier = Modifier.size(300.dp, 70.dp),
            color = ToastBackground,
            shape = RoundedCornerShape(4.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = text,
                    color = Foreground,
                )
            }
            LaunchedEffect(Unit) {
                delay(duration.value.toLong())
                onDismiss()
            }
        }
    }
}
