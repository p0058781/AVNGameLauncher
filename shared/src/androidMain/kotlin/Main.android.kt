import androidx.compose.runtime.Composable
import org.skynetsoftware.avnlauncher.ui.screen.MainScreen

@Composable fun MainView(exitApplication: () -> Unit) = MainScreen(exitApplication = exitApplication)
