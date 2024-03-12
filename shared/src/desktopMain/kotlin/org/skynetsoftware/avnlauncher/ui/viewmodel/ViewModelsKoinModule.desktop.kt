package org.skynetsoftware.avnlauncher.ui.viewmodel

import com.jthemedetecor.OsThemeDetector
import org.koin.dsl.module

actual val viewModelsKoinModule = module {
    factory<GamesViewModel> { GamesViewModel(get(), get(), get(), get(), get(), get()) }
    factory<EditGameViewModel> { EditGameViewModel(get(), get()) }
    factory<MainViewModel> { MainViewModel(get(), get(), get(), get(), OsThemeDetector.getDetector()) }
    factory<SettingsViewModel> { SettingsViewModel(get()) }
    factory<ImportGameViewModel> { ImportGameViewModel(get()) }
}
