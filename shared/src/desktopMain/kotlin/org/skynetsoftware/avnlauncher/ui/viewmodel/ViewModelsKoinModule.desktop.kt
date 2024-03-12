package org.skynetsoftware.avnlauncher.ui.viewmodel

import org.koin.dsl.module

actual val viewModelsKoinModule = module {
    factory<GamesViewModel> { GamesViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory<EditGameViewModel> { EditGameViewModel(get(), get()) }
    factory<MainViewModel> { MainViewModel(get(), get(), get(), get()) }
    factory<SettingsViewModel> { SettingsViewModel(get()) }
}
