package org.skynetsoftware.avnlauncher.ui.screen.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.rememberImagePainter
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsAveragePlayTime30Days
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsAveragePlayTime30DaysLabel
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsAveragePlayTime365Days
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsAveragePlayTime365DaysLabel
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsAveragePlayTime7Days
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsAveragePlayTime7DaysLabel
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsAveragePlayTimeTotal
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsAveragePlayTimeTotalLabel
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsError
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsPlayTimeByVersion
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsPlayTimeLast30Days
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsPlayTimeLast365Days
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsPlayTimeLast7Days
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsPlayTimeToday
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsTabEdit
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsTabOverview
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsTabStatistics
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsTotalPlayTime
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsVersionLabel
import org.skynetsoftware.avnlauncher.app.generated.resources.gameDetailsVersionOther
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.GameWithStats
import org.skynetsoftware.avnlauncher.imageloader.ImageLoaderFactory
import org.skynetsoftware.avnlauncher.ui.screen.editgame.EditGameScreen
import org.skynetsoftware.avnlauncher.ui.screen.main.games.InfoItem
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModel
import org.skynetsoftware.avnlauncher.utils.formatPlayTime

private const val TAB_INDEX_OVERVIEW = 0
private const val TAB_INDEX_STATISTICS = 1
private const val TAB_INDEX_EDIT = 2

private const val IMAGE_ASPECT_RATIO = 5f

@Composable
fun GameDetailsScreen(
    gameId: Int,
    gameDetailsViewModel: GameDetailsViewModel = viewModel { parametersOf(gameId) },
    imageLoaderFactory: ImageLoaderFactory = koinInject(),
) {
    val loadingState by remember { gameDetailsViewModel.state }
        .collectAsState(GameDetailsViewModel.LoadingState.Loading)

    val imageLoader by remember {
        gameDetailsViewModel.showGifs.map { imageLoaderFactory.createImageLoader(it) }
    }.collectAsState(imageLoaderFactory.createImageLoader(false))

    var tabIndex by remember { mutableStateOf(TAB_INDEX_OVERVIEW) }

    Surface(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    ) {
        when (val loadingStateLocal = loadingState) {
            GameDetailsViewModel.LoadingState.Error -> {
                Box {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(Res.string.gameDetailsError),
                    )
                }
            }

            GameDetailsViewModel.LoadingState.Loading -> {
                CircularProgressIndicator()
            }

            is GameDetailsViewModel.LoadingState.Ready -> {
                val game = loadingStateLocal.game
                CompositionLocalProvider(
                    LocalImageLoader provides imageLoader,
                ) {
                    Column {
                        Image(
                            painter = rememberImagePainter(
                                request = ImageRequest { data(game.game.imageUrl) },
                            ),
                            contentDescription = null,
                            modifier = Modifier.aspectRatio(IMAGE_ASPECT_RATIO),
                            contentScale = ContentScale.Crop,
                        )
                        TabRow(
                            selectedTabIndex = tabIndex,
                            tabs = {
                                Tab(
                                    title = stringResource(Res.string.gameDetailsTabOverview),
                                    selectedTabIndex = tabIndex,
                                    tabIndex = TAB_INDEX_OVERVIEW,
                                    onClick = {
                                        tabIndex = TAB_INDEX_OVERVIEW
                                    },
                                )
                                Tab(
                                    title = stringResource(Res.string.gameDetailsTabStatistics),
                                    selectedTabIndex = tabIndex,
                                    tabIndex = TAB_INDEX_STATISTICS,
                                    onClick = {
                                        tabIndex = TAB_INDEX_STATISTICS
                                    },
                                )
                                Tab(
                                    title = stringResource(Res.string.gameDetailsTabEdit),
                                    selectedTabIndex = tabIndex,
                                    tabIndex = TAB_INDEX_EDIT,
                                    onClick = {
                                        tabIndex = TAB_INDEX_EDIT
                                    },
                                )
                            },
                        )
                        when (tabIndex) {
                            TAB_INDEX_OVERVIEW -> TabOverview(game.game)
                            TAB_INDEX_STATISTICS -> TabStatistics(game)
                            TAB_INDEX_EDIT -> TabEdit(game.game.f95ZoneThreadId)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Tab(
    title: String,
    selectedTabIndex: Int,
    tabIndex: Int,
    onClick: () -> Unit,
) {
    Tab(
        selected = selectedTabIndex == tabIndex,
        onClick = onClick,
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(10.dp),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
private fun TabOverview(game: Game) {
    Column(
        modifier = Modifier.padding(10.dp),
    ) {
        Text(
            text = "${game.title} [${game.version}] [${game.developer}]",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            modifier = Modifier,
        )
        FlowRow {
            game.tags.forEach {
                Chip(
                    modifier = Modifier.padding(horizontal = 2.dp),
                    onClick = {},
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.caption,
                    )
                }
            }
        }
        Text(
            text = game.description,
        )
    }
}

@Composable
private fun TabStatistics(gameWithStats: GameWithStats) {
    Column(
        modifier = Modifier.padding(10.dp),
    ) {
        Row {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                InfoItem(
                    label = stringResource(Res.string.gameDetailsTotalPlayTime),
                    value = formatPlayTime(gameWithStats.totalPlayTime),
                )
                InfoItem(
                    label = stringResource(Res.string.gameDetailsPlayTimeToday),
                    value = formatPlayTime(gameWithStats.playTimeToday),
                )
                InfoItem(
                    label = stringResource(Res.string.gameDetailsPlayTimeLast7Days),
                    value = formatPlayTime(gameWithStats.playTime7Days),
                )
                InfoItem(
                    label = stringResource(Res.string.gameDetailsPlayTimeLast30Days),
                    value = formatPlayTime(gameWithStats.playTime30Days),
                )
                InfoItem(
                    label = stringResource(Res.string.gameDetailsPlayTimeLast365Days),
                    value = formatPlayTime(gameWithStats.playTime365Days),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
            ) {
                InfoItem(
                    label = stringResource(Res.string.gameDetailsAveragePlayTimeTotalLabel),
                    value = stringResource(Res.string.gameDetailsAveragePlayTimeTotal)
                        .format(gameWithStats.averagePlayTimeHours),
                )
                InfoItem(
                    label = stringResource(Res.string.gameDetailsAveragePlayTime7DaysLabel),
                    value = stringResource(Res.string.gameDetailsAveragePlayTime7Days)
                        .format(gameWithStats.averagePlayTimeHours7Days),
                )
                InfoItem(
                    label = stringResource(Res.string.gameDetailsAveragePlayTime30DaysLabel),
                    value = stringResource(Res.string.gameDetailsAveragePlayTime30Days)
                        .format(gameWithStats.averagePlayTimeHours30Days),
                )
                InfoItem(
                    label = stringResource(Res.string.gameDetailsAveragePlayTime365DaysLabel),
                    value = stringResource(Res.string.gameDetailsAveragePlayTime365Days)
                        .format(gameWithStats.averagePlayTimeHours365Days),
                )
            }
        }
        Spacer(
            modifier = Modifier.height(20.dp),
        )
        Text(
            text = stringResource(Res.string.gameDetailsPlayTimeByVersion),
        )
        Column {
            gameWithStats.totalPlayTimeByVersion.forEach { (t, u) ->
                InfoItem(
                    label = stringResource(Res.string.gameDetailsVersionLabel).format(
                        t ?: stringResource(Res.string.gameDetailsVersionOther),
                    ),
                    value = formatPlayTime(u),
                )
            }
        }
    }
}

@Composable
private fun TabEdit(gameId: Int) {
    EditGameScreen(gameId) {
    }
}
