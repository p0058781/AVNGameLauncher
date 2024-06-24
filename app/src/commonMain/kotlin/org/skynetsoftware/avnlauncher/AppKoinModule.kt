package org.skynetsoftware.avnlauncher

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.Config
import org.skynetsoftware.avnlauncher.domain.usecase.ImportGameUseCase

val appKoinModule = module {
    single {
        ImportGameUseCase(get(), get(), get())
    }
}

fun configKoinModule(config: Config) =
    module {
        single<Config> { config }
    }
