package org.skynetsoftware.avnlauncher.ui.viewmodel

import androidx.compose.runtime.Composable
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.ParametersDefinition
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.ui.screen.customlists.CustomListsViewModel
import org.skynetsoftware.avnlauncher.ui.screen.customstatuses.CustomStatusesViewModel
import org.skynetsoftware.avnlauncher.ui.screen.editgame.ManageGameViewModel
import org.skynetsoftware.avnlauncher.ui.screen.game.GameDetailsViewModel
import org.skynetsoftware.avnlauncher.ui.screen.import.ImportGameViewModel
import org.skynetsoftware.avnlauncher.ui.screen.main.MainScreenViewModel
import org.skynetsoftware.avnlauncher.ui.screen.settings.SettingsViewModel

val viewModelsKoinModule = module {
    factory { MainScreenViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { parameters -> ManageGameViewModel(parameters.get(), get(), get(), get(), get(), get(), get()) }
    factory { parameters -> GameDetailsViewModel(parameters.get(), get(), get()) }
    factory { SettingsViewModel(get(), get(), get()) }
    factory { ImportGameViewModel(get(), get(), get()) }
    factory { CustomStatusesViewModel(get(), get()) }
    factory { CustomListsViewModel(get(), get()) }
}

@Composable
inline fun <reified T : ViewModel> viewModel(noinline parameters: ParametersDefinition? = null): T {
    val viewModel = koinInject<T>(parameters = parameters)
    return getViewModel(T::class.java.name, viewModelFactory { viewModel })
}
