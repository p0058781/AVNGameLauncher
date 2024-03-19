package org.skynetsoftware.avnlauncher.ui.viewmodel

import androidx.compose.runtime.Composable
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.ui.screen.editgame.EditGameViewModel
import org.skynetsoftware.avnlauncher.ui.screen.import.ImportGameViewModel
import org.skynetsoftware.avnlauncher.ui.screen.main.MainScreenViewModel
import org.skynetsoftware.avnlauncher.ui.screen.settings.SettingsViewModel

actual val viewModelsKoinModule = module {
    single(named<MainScreenViewModel>()) {
        viewModelFactory<MainScreenViewModel> { MainScreenViewModel(get(), get(), get(), get(), get(), get(), get()) }
    }
    single(named<EditGameViewModel>()) { parameters ->
        viewModelFactory<EditGameViewModel> {
            EditGameViewModel(parameters.get(), get(), get(), get())
        }
    }
    single(named<SettingsViewModel>()) { viewModelFactory<SettingsViewModel> { SettingsViewModel(get()) } }
    single(named<ImportGameViewModel>()) {
        viewModelFactory<ImportGameViewModel> { ImportGameViewModel(get(), get(), get()) }
    }
}

@Composable
actual inline fun <reified T : ViewModel> viewModel(noinline parameters: ParametersDefinition?): T =
    getViewModel(T::class.java.name, koinInject(qualifier = named<T>(), parameters = parameters))
