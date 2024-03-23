package org.skynetsoftware.avnlauncher.ui.viewmodel

import androidx.compose.runtime.Composable
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.ParametersDefinition
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.ui.screen.editgame.ManageGameViewModel
import org.skynetsoftware.avnlauncher.ui.screen.import.ImportGameViewModel
import org.skynetsoftware.avnlauncher.ui.screen.main.MainScreenViewModel
import org.skynetsoftware.avnlauncher.ui.screen.settings.SettingsViewModel

actual val viewModelsKoinModule = module {
    factory { MainScreenViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { parameters -> ManageGameViewModel(parameters.get(), get(), get(), get()) }
    factory { SettingsViewModel(get(), get()) }
    factory { ImportGameViewModel(get(), get(), get()) }
}

@Composable
actual inline fun <reified T : ViewModel> viewModel(noinline parameters: ParametersDefinition?): T {
    val viewModel = koinInject<T>(parameters = parameters)
    return getViewModel(T::class.java.name, viewModelFactory { viewModel })
}
