package org.skynetsoftware.avnlauncher.data.settings

import com.russhwolf.settings.Settings
import org.koin.dsl.module

val settingsKoinModule = module {
    single<SettingsManager> { SettingsManagerImpl(Settings()) }
}

interface SettingsManager {
}

class SettingsManagerImpl(settings: Settings): SettingsManager

//TODO selected filter and sort order
//TODO import game executable search location for desktop