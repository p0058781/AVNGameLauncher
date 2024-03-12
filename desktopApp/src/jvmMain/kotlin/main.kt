import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.skynetsoftware.avnlauncher.AVNLauncherApp
import java.awt.Dimension
import java.awt.Toolkit

fun main() {
    AVNLauncherApp.onCreate()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "AVN Game Launcher",
            icon = painterResource("icon.png"),
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getDefaultWindowSize()
            ),
        ) {
            MainView()
        }
    }
}

fun getDefaultWindowSize(): DpSize {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    val width: Int = (screenSize.width * 0.6f).toInt()
    val height: Int = (screenSize.height * 0.6f).toInt()
    return DpSize(width.dp, height.dp)
}