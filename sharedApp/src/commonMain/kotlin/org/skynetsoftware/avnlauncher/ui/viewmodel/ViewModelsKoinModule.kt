package org.skynetsoftware.avnlauncher.ui.viewmodel

import org.koin.dsl.module

val viewModelsKoinModule = module {
    factory<GamesScreenModel> { GamesScreenModel(get(), get(), get(), get(), get()) }
    factory<EditGameScreenModel> { EditGameScreenModel(get(), get()) }
    factory<MainScreenModel> { MainScreenModel(get(), get(), get(), get()) }
    factory<SettingsScreenModel> { SettingsScreenModel(get()) }
    factory<ImportGameScreenModel> { ImportGameScreenModel(get()) }
}
