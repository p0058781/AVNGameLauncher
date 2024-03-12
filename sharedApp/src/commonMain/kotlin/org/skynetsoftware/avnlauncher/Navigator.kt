package org.skynetsoftware.avnlauncher

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.ui.screen.MainScreen
import org.skynetsoftware.avnlauncher.ui.theme.darkColors
import org.skynetsoftware.avnlauncher.ui.theme.lightColors
import org.skynetsoftware.avnlauncher.ui.viewmodel.MainScreenModel

typealias DraggableArea = @Composable (content: @Composable () -> Unit) -> Unit

val LocalDraggableArea: ProvidableCompositionLocal<DraggableArea?> = staticCompositionLocalOf { null }

@Composable
fun Navigator(
    mainScreenModel: MainScreenModel = koinInject(),
    exitApplication: () -> Unit,
    draggableArea: DraggableArea,
) {
    CompositionLocalProvider(LocalDraggableArea provides draggableArea) {
        Navigator(
            MainScreen(
                exitApplication = exitApplication,
            ),
        ) {
            val forceDarkTheme by mainScreenModel.forceDarkTheme.collectAsState()
            MaterialTheme(
                colors = if (isSystemInDarkTheme() || forceDarkTheme) darkColors else lightColors,
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CurrentScreen()
                }
            }
        }
    }
}
