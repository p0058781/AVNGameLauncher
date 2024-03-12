package org.skynetsoftware.avnlauncher.ui.viewmodel

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

actual val viewModelsKoinModule = module {
    viewModel<GamesViewModel> { GamesViewModel(get(), get(), get(), get()) }
    viewModel<EditGameViewModel> { EditGameViewModel(get(), get()) }
    viewModel<MainViewModel> { MainViewModel(get(), get()) }
}
