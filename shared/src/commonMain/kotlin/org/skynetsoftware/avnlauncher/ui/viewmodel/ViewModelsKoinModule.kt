package org.skynetsoftware.avnlauncher.ui.viewmodel

import org.koin.dsl.module

val viewModelsKoinModule = module {
    single<GamesViewModel> { GamesViewModel(get()) }
    //TODO on android we would use viewModel here, any implications of using singe?
}