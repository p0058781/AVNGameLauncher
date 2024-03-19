package org.skynetsoftware.avnlauncher

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.back
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameDialogTitleNoGameTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.settings
import org.skynetsoftware.avnlauncher.ui.screen.editgame.EditGameScreen
import org.skynetsoftware.avnlauncher.ui.screen.import.ImportGameScreen
import org.skynetsoftware.avnlauncher.ui.screen.main.MainScreen
import org.skynetsoftware.avnlauncher.ui.screen.settings.SettingsScreen
import org.skynetsoftware.avnlauncher.ui.theme.darkColors

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun App() {
    MaterialTheme(
        colors = darkColors,
    ) {
        val navController = rememberNavController()

        CompositionLocalProvider(
            value = LocalNavigator provides Navigator(
                navigateToSettings = { navController.navigate("settingsScreen") },
                navigateToImportGame = { navController.navigate("importGameScreen") },
                navigateToEditGame = { navController.navigate("editGameScreen/${it.f95ZoneThreadId}") },
            ),
        ) {
            NavHost(navController = navController, startDestination = "mainScreen") {
                composable("mainScreen") {
                    MainScreen()
                }
                composable("settingsScreen") {
                    WithToolbar(
                        title = stringResource(Res.string.settings),
                        navController = navController,
                    ) {
                        SettingsScreen()
                    }
                }
                composable("importGameScreen") {
                    WithToolbar(
                        title = stringResource(Res.string.importGameDialogTitle),
                        navController = navController,
                    ) {
                        ImportGameScreen {
                            navController.popBackStack()
                        }
                    }
                }
                composable("editGameScreen/{id}") { backStackEntry ->
                    WithToolbar(
                        title = stringResource(Res.string.editGameDialogTitleNoGameTitle),
                        navController = navController,
                    ) {
                        backStackEntry.arguments?.getString("id")?.toIntOrNull()?.let {
                            EditGameScreen(it) {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun WithToolbar(
    title: String,
    navController: NavController,
    content: @Composable () -> Unit,
) {
    Column {
        TopAppBar(
            title = {
                Text(
                    text = title,
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        navController.navigateUp()
                    },
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(Res.drawable.back),
                        contentDescription = null,
                    )
                }
            },
        )
        content()
    }
}
