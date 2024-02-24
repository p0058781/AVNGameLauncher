package org.skynetsoftware.avnlauncher

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import org.skynetsoftware.avnlauncher.ui.screen.main.MainScreen
import org.skynetsoftware.avnlauncher.ui.theme.darkColors

typealias DraggableArea = @Composable (content: @Composable () -> Unit) -> Unit

val LocalDraggableArea: ProvidableCompositionLocal<DraggableArea?> = staticCompositionLocalOf { null }

@Composable
fun Navigator(
    exitApplication: () -> Unit,
    draggableArea: DraggableArea,
) {
    CompositionLocalProvider(LocalDraggableArea provides draggableArea) {
        Navigator(
            MainScreen(
                exitApplication = exitApplication,
            ),
        ) {
            MaterialTheme(
                colors = darkColors,
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
