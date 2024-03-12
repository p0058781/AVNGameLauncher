import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import org.skynetsoftware.avnlauncher.ui.screen.MainScreen

@Composable
fun MainView(
    exitApplication: () -> Unit,
    draggableArea: @Composable (content: @Composable () -> Unit) -> Unit,
) = MainScreen(exitApplication = exitApplication, draggableArea = draggableArea)

@Preview
@Composable
fun AppPreview() {
    MainScreen(exitApplication = {}, draggableArea = {})
}
