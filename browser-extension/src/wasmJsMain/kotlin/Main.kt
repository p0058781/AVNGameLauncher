import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.Res
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.addGame
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.added
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.archived
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.favorites
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.firstPlayed
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.haveLatestVersion
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.lastPlayed
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.myRating
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.nHours
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.nMinutes
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.nSeconds
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.no
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.noValue
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.notes
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.playState
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.totalPlayTime
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.unknownError
import org.skynetsoftware.avnlauncher.browser_extension.generated.resources.yes
import org.skynetsoftware.avnlauncher.extension.ViewModel
import org.skynetsoftware.avnlauncher.extension.model.DisplayableError
import org.skynetsoftware.avnlauncher.extension.model.GameDto
import org.skynetsoftware.avnlauncher.extension.repository.GameRepository

private const val ONE_SECOND_MILLIS = 1000L
private const val ONE_HOUR_SECONDS = 3600L
private const val ONE_MINUTE_SECONDS = 60L

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val viewModel = ViewModel(GameRepository())

    ComposeViewport(document.body!!) {
        MaterialTheme(
            colors = if (isSystemInDarkTheme()) darkColors() else lightColors(),
        ) {
            Surface(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            ) {
                val uiState by remember { viewModel.uiState }.collectAsState(ViewModel.UiState.Loading)
                val error by remember { viewModel.error }.collectAsState()
                val game by remember { viewModel.game }.collectAsState()
                when (uiState) {
                    ViewModel.UiState.Loading -> {
                        LoadingScreen()
                    }

                    ViewModel.UiState.Error -> {
                        ErrorScreen(error)
                    }

                    ViewModel.UiState.GameDetails -> {
                        game?.let {
                            GameDetails(it)
                        }
                    }

                    ViewModel.UiState.AddGame -> {
                        AddGame {
                            viewModel.addGame()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun ErrorScreen(error: DisplayableError?) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center).padding(10.dp),
            textAlign = TextAlign.Center,
            text = when (error) {
                is DisplayableError.ResError -> stringResource(error.error)
                is DisplayableError.StringError -> error.error
                null -> stringResource(Res.string.unknownError)
            },
        )
    }
}

@Composable
private fun GameDetails(game: GameDto) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
    ) {
        InfoItem(stringResource(Res.string.myRating), game.rating.toString())
        InfoItem(stringResource(Res.string.haveLatestVersion), (!game.updateAvailable).yesNo())
        InfoItem(stringResource(Res.string.archived), game.hidden.yesNo())
        InfoItem(stringResource(Res.string.playState), game.playState)
        InfoItem(stringResource(Res.string.notes), game.notes)
        InfoItem(stringResource(Res.string.favorites), game.favorite.yesNo())
        InfoItem(stringResource(Res.string.totalPlayTime), formatPlayTime(game.totalPlayTime))
        InfoItem(stringResource(Res.string.added), formatDateTime(game.added))
        InfoItem(stringResource(Res.string.firstPlayed), formatDateTime(game.firstPlayedTime))
        InfoItem(stringResource(Res.string.lastPlayed), formatDateTime(game.lastPlayedTime))
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String?,
) {
    if (!value.isNullOrBlank()) {
        Text("$label: $value")
    }
}

@Composable
private fun AddGame(addGame: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = {
                addGame()
            },
        ) {
            Text(stringResource(Res.string.addGame))
        }
    }
}

@Composable
private fun Boolean.yesNo(): String {
    return stringResource(
        if (this) {
            Res.string.yes
        } else {
            Res.string.no
        },
    )
}

private fun formatDateTime(dateTime: Long): String {
    val dateFormat = LocalDateTime.Format {
        dayOfMonth()
        char('/')
        monthNumber(padding = Padding.SPACE)
        char('/')
        year()
    }
    return Instant.fromEpochMilliseconds(dateTime).toLocalDateTime(TimeZone.currentSystemDefault()).format(dateFormat)
}

@Composable
fun formatPlayTime(playTime: Long?): String {
    if (playTime == null || playTime == 0L) {
        return stringResource(Res.string.noValue)
    }
    val totalSeconds = playTime / ONE_SECOND_MILLIS

    // Calculate hours, minutes, and seconds
    val hours = totalSeconds / ONE_HOUR_SECONDS
    val minutes = (totalSeconds % ONE_HOUR_SECONDS) / ONE_MINUTE_SECONDS
    val seconds = totalSeconds % ONE_MINUTE_SECONDS

    // Format the result
    return when {
        hours > 0 -> stringResource(Res.string.nHours, hours)
        minutes > 0 -> stringResource(Res.string.nMinutes, minutes)
        else -> stringResource(Res.string.nSeconds, seconds)
    }
}
