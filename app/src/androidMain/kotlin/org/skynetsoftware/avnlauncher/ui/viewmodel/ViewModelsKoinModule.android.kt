package org.skynetsoftware.avnlauncher.ui.viewmodel

import androidx.compose.runtime.Composable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.ui.screen.editgame.ManageGameViewModel
import org.skynetsoftware.avnlauncher.ui.screen.import.ImportGameViewModel
import org.skynetsoftware.avnlauncher.ui.screen.main.MainScreenViewModel
import org.skynetsoftware.avnlauncher.ui.screen.settings.SettingsViewModel

actual val viewModelsKoinModule = module {
    viewModel<MainScreenViewModel> { MainScreenViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel<ManageGameViewModel> { ManageGameViewModel(get(), get(), get(), get()) }
    viewModel<SettingsViewModel> { SettingsViewModel(get(), get()) }
    viewModel<ImportGameViewModel> { ImportGameViewModel(get(), get(), get()) }
}

@Suppress("MaxLineLength") // conflict between ktlint and detekt
@Composable
actual inline fun <reified T : ViewModel> viewModel(noinline parameters: ParametersDefinition?): T = koinViewModel(parameters = parameters)
