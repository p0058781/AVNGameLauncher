import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import org.skynetsoftware.avnlauncher.Navigator
import org.skynetsoftware.avnlauncher.ui.screen.main.MainScreen

@Composable
fun MainView(
    exitApplication: () -> Unit,
    draggableArea: @Composable (content: @Composable () -> Unit) -> Unit,
    setMaximized: () -> Unit,
    setFloating: () -> Unit,
) = Navigator(
    exitApplication = exitApplication,
    draggableArea = draggableArea,
    setMaximized = setMaximized,
    setFloating = setFloating,
)

@Preview
@Composable
fun AppPreview() {
    MainScreen(exitApplication = {}, setMaximized = {}, setFloating = {})
}
