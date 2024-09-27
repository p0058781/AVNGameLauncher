package org.skynetsoftware.avnlauncher

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.back
import org.skynetsoftware.avnlauncher.app.generated.resources.cardValuesScreenTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.createCustomGameTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.customListsScreenTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.customStatusesScreenTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.editGameDialogTitleNoGameTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.importExportScreenTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.importGameDialogTitle
import org.skynetsoftware.avnlauncher.app.generated.resources.settingsTitle
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.ui.screen.cardvalues.CardValuesScreen
import org.skynetsoftware.avnlauncher.ui.screen.customlists.CustomListsScreen
import org.skynetsoftware.avnlauncher.ui.screen.customstatuses.CustomStatusesScreen
import org.skynetsoftware.avnlauncher.ui.screen.editgame.CreateCustomGameScreen
import org.skynetsoftware.avnlauncher.ui.screen.editgame.EditGameScreen
import org.skynetsoftware.avnlauncher.ui.screen.import.ImportGameScreen
import org.skynetsoftware.avnlauncher.ui.screen.importexport.ImportExportScreen
import org.skynetsoftware.avnlauncher.ui.screen.main.MainScreen
import org.skynetsoftware.avnlauncher.ui.screen.settings.SettingsScreen
import org.skynetsoftware.avnlauncher.ui.theme.darkColors

private const val MAIN_SCREEN_ROUTE = "mainScreen"
private const val SETTINGS_SCREEN_ROUTE = "settingsScreen"
private const val CREATE_CUSTOM_GAME_SCREEN_ROUTE = "createCustomGameScreen"
private const val IMPORT_GAME_SCREEN_ROUTE = "importGameScreen"
private const val GAME_DETAILS_SCREEN_ROUTE = "gameDetailsScreen"
private const val GAME_DETAILS_SCREEN_PARAM_ID = "id"
private const val IMPORT_EXPORT_SCREEN_ROUTE = "importExportScreen"
private const val CUSTOM_LISTS_SCREEN_ROUTE = "customListsScreen"
private const val CUSTOM_STATUSES_SCREEN_ROUTE = "customStatusesScreen"
private const val CARD_VALUES_SCREEN_ROUTE = "cardValuesScreen"

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun App() {
    MaterialTheme(
        colors = darkColors,
    ) {
        val navController = rememberNavController()

        CompositionLocalProvider(
            value = LocalNavigator provides AndroidNavigator(navController),
        ) {
            NavHost(navController = navController, startDestination = MAIN_SCREEN_ROUTE) {
                composable(MAIN_SCREEN_ROUTE) {
                    MainScreen()
                }
                composable(SETTINGS_SCREEN_ROUTE) {
                    WithToolbar(
                        title = stringResource(Res.string.settingsTitle),
                        navController = navController,
                    ) {
                        SettingsScreen()
                    }
                }
                composable(IMPORT_GAME_SCREEN_ROUTE) {
                    WithToolbar(
                        title = stringResource(Res.string.importGameDialogTitle),
                        navController = navController,
                    ) {
                        ImportGameScreen {
                            navController.popBackStack()
                        }
                    }
                }
                composable("$GAME_DETAILS_SCREEN_ROUTE/{$GAME_DETAILS_SCREEN_PARAM_ID}") { backStackEntry ->
                    WithToolbar(
                        title = stringResource(Res.string.editGameDialogTitleNoGameTitle),
                        navController = navController,
                    ) {
                        backStackEntry.arguments?.getString(GAME_DETAILS_SCREEN_PARAM_ID)?.toIntOrNull()?.let {
                            EditGameScreen(it) {
                                navController.popBackStack()
                            }
                        }
                    }
                }
                composable(CREATE_CUSTOM_GAME_SCREEN_ROUTE) {
                    WithToolbar(
                        title = stringResource(Res.string.createCustomGameTitle),
                        navController = navController,
                    ) {
                        CreateCustomGameScreen {
                            navController.popBackStack()
                        }
                    }
                }
                composable(IMPORT_EXPORT_SCREEN_ROUTE) {
                    WithToolbar(
                        title = stringResource(Res.string.importExportScreenTitle),
                        navController = navController,
                    ) {
                        ImportExportScreen()
                    }
                }
                composable(CUSTOM_LISTS_SCREEN_ROUTE) {
                    WithToolbar(
                        title = stringResource(Res.string.customListsScreenTitle),
                        navController = navController,
                    ) {
                        CustomListsScreen()
                    }
                }
                composable(CUSTOM_STATUSES_SCREEN_ROUTE) {
                    WithToolbar(
                        title = stringResource(Res.string.customStatusesScreenTitle),
                        navController = navController,
                    ) {
                        CustomStatusesScreen()
                    }
                }
                composable(CARD_VALUES_SCREEN_ROUTE) {
                    WithToolbar(
                        title = stringResource(Res.string.cardValuesScreenTitle),
                        navController = navController,
                    ) {
                        CardValuesScreen()
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
                        modifier = Modifier,
                        painter = painterResource(Res.drawable.back),
                        contentDescription = null,
                    )
                }
            },
        )
        content()
    }
}

private class AndroidNavigator(private val navController: NavController) : Navigator {
    override fun navigateToSettings() {
        navController.navigate(SETTINGS_SCREEN_ROUTE)
    }

    override fun navigateToGameDetails(game: Game) {
        navController.navigate("$GAME_DETAILS_SCREEN_PARAM_ID/${game.f95ZoneThreadId}")
    }

    override fun navigateToCreateCustomGame() {
        navController.navigate(CREATE_CUSTOM_GAME_SCREEN_ROUTE)
    }

    override fun navigateToImportGame() {
        navController.navigate(IMPORT_GAME_SCREEN_ROUTE)
    }

    override fun navigateToImportExport() {
        navController.navigate(IMPORT_EXPORT_SCREEN_ROUTE)
    }

    override fun navigateToCustomLists() {
        navController.navigate(CUSTOM_LISTS_SCREEN_ROUTE)
    }

    override fun navigateToCustomStatuses() {
        navController.navigate(CUSTOM_STATUSES_SCREEN_ROUTE)
    }

    override fun navigateToCardValues() {
        navController.navigate(CARD_VALUES_SCREEN_ROUTE)
    }
}
