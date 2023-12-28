import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.skynetsoftware.avnlauncher.AVNLauncherApp
import org.skynetsoftware.avnlauncher.resources.R
import java.awt.Dimension
import java.awt.Toolkit
import java.lang.reflect.Field

fun main() {
    try {
        val xToolkit = Toolkit.getDefaultToolkit()
        val awtAppClassNameField: Field = xToolkit.javaClass.getDeclaredField("awtAppClassName")
        awtAppClassNameField.setAccessible(true)
        awtAppClassNameField.set(xToolkit, R.strings.appName)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    AVNLauncherApp.onCreate()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = R.strings.appName,
            icon = painterResource(R.images.appIcon),
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getDefaultWindowSize(),
            ),
            undecorated = true,
        ) {
            MainView {
                exitApplication()
            }
        }
    }
}

fun getDefaultWindowSize(): DpSize {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    val width: Int = (screenSize.width * 0.7f).toInt()
    val height: Int = (screenSize.height * 0.72f).toInt()
    return DpSize(width.dp, height.dp)
}
