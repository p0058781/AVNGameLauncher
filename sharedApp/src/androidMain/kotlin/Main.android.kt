import androidx.compose.runtime.Composable
import org.skynetsoftware.avnlauncher.Navigator

@Composable
fun MainView(exitApplication: () -> Unit) {
    Navigator(exitApplication = exitApplication, draggableArea = { it() })
}
