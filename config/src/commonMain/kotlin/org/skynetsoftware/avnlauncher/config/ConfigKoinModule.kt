package org.skynetsoftware.avnlauncher.config

import org.koin.dsl.module

fun configKoinModule(config: Config) =
    module {
        single<Config> { config }
    }
