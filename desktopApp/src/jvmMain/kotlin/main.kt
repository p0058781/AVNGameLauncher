import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.skynetsoftware.avnlauncher.AVNLauncherApp
import org.skynetsoftware.avnlauncher.utils.getDefaultWindowSize

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