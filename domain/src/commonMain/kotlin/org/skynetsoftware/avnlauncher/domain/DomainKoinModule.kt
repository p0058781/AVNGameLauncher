package org.skynetsoftware.avnlauncher.domain

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.domain.coroutines.AvnLauncherCoroutineDispatchers
import org.skynetsoftware.avnlauncher.domain.coroutines.CoroutineDispatchers

val domainKoinModule = module {
    single<CoroutineDispatchers> { AvnLauncherCoroutineDispatchers() }
}
