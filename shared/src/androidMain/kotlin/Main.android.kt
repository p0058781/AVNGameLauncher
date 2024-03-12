import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import org.skynetsoftware.avnlauncher.ui.screen.MainScreen

@Composable
fun MainView(exitApplication: () -> Unit) {
    Navigator(MainScreen(exitApplication = exitApplication, draggableArea = { it() }))
}
