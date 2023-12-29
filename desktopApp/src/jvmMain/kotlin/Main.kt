import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.skynetsoftware.avnlauncher.AVNLauncherApp
import org.skynetsoftware.avnlauncher.resources.R
import org.skynetsoftware.avnlauncher.utils.OS
import org.skynetsoftware.avnlauncher.utils.os
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
        if (os == OS.Mac) {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Fuck you apple",
                icon = painterResource(R.images.appIcon),
                state = WindowState(
                    position = WindowPosition.Aligned(Alignment.Center),
                    size = DpSize(300.dp, 150.dp),
                ),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(10.dp),
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                    ) {
                        Text("Apple is not supported!!! Fuck Apple")
                        Text("Anyone who is involved in development of apple products should burn in hell for all eternity")
                    }
                }
            }
        } else {
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
                MainView(
                    exitApplication = {
                        exitApplication()
                    },
                    draggableArea = { content ->
                        WindowDraggableArea {
                            content()
                        }
                    },
                )
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
