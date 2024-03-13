package org.skynetsoftware.avnlauncher.ui.screen

import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dev.icerock.moko.resources.compose.stringResource
import org.skynetsoftware.avnlauncher.MR

@Composable
actual fun GamePicker(
    show: Boolean,
    currentExecutable: String?,
    onGamePicked: (game: String?) -> Unit,
) {
    val mainIntent = Intent(Intent.ACTION_MAIN, null)
    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
    val activities: List<ResolveInfo> =
        LocalContext.current.packageManager.queryIntentActivities(mainIntent, 0).filter {
            it.activityInfo.name == "org.renpy.android.PythonSDLActivity"
        }

    var visible by remember { mutableStateOf(show) }

    if (visible) {
        Dialog(
            title = stringResource(MR.strings.gamePickerPickGame),
            onDismiss = {
                visible = false
                onGamePicked(null)
            },
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
            ) {
                LazyColumn(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    items(activities) {
                        Item(it, onGamePicked)
                    }
                }
            }
        }
    }
}

@Composable
private fun Item(
    resolveInfo: ResolveInfo,
    onGamePicked: (game: String?) -> Unit,
) {
    val icon = resolveInfo.loadIcon(LocalContext.current.packageManager)
    val name = resolveInfo.loadLabel(LocalContext.current.packageManager)
    val packageName = resolveInfo.activityInfo.packageName

    Row(
        modifier = Modifier.clickable {
            onGamePicked(packageName)
        },
    ) {
        Image(
            modifier = Modifier.size(48.dp),
            painter = rememberDrawablePainter(icon),
            contentDescription = "icon",
        )
        Spacer(
            modifier = Modifier.width(10.dp),
        )
        Column(
            modifier = Modifier,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = name.toString(),
                maxLines = 1,
                color = MaterialTheme.colors.onSurface,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = packageName.toString(),
                maxLines = 1,
            )
        }
    }
}

@Composable
actual fun GamesDirPicker(
    visible: Boolean,
    currentDir: String?,
    onDirPicked: (dir: String?) -> Unit,
) {
    error("GamesDirPicker is not supported on android")
}
