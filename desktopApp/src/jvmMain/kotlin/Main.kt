import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import org.skynetsoftware.avnlauncher.AVNLauncherApp
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.resources.R
import java.awt.Dimension
import java.awt.Toolkit
import java.lang.reflect.Field

private const val DEFAULT_WINDOW_WIDTH_PERCENT = 0.7f
private const val DEFAULT_WINDOW_HEIGHT_PERCENT = 0.72f

@Suppress("TooGenericExceptionCaught")
fun main() {
    try {
        val xToolkit = Toolkit.getDefaultToolkit()
        val awtAppClassNameField: Field = xToolkit.javaClass.getDeclaredField("awtAppClassName")
        awtAppClassNameField.setAccessible(true)
        awtAppClassNameField.set(xToolkit, StringDesc.Resource(MR.strings.appName).localized())
    } catch (e: Exception) {
        @Suppress("PrintStackTrace")
        e.printStackTrace()
    }

    AVNLauncherApp.onCreate()
    application {
        val windowState = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getDefaultWindowSize(),
        )
        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(MR.strings.appName),
            icon = painterResource(R.images.appIcon),
            state = windowState,
            undecorated = true,
        ) {
            MainView(
                exitApplication = {
                    exitApplication()
                },
                draggableArea = { content ->
                    WindowDraggableArea {
                        content()
                    }
                },
                setMaximized = {
                    windowState.placement = WindowPlacement.Maximized
                },
                setFloating = {
                    windowState.placement = WindowPlacement.Floating
                },
            )
        }
    }
}

fun getDefaultWindowSize(): DpSize {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    val width: Int = (screenSize.width * DEFAULT_WINDOW_WIDTH_PERCENT).toInt()
    val height: Int = (screenSize.height * DEFAULT_WINDOW_HEIGHT_PERCENT).toInt()
    return DpSize(width.dp, height.dp)
}
