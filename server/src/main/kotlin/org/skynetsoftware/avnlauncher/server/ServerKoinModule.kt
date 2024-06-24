package org.skynetsoftware.avnlauncher.server

import org.koin.dsl.module

val httpServerKoinModule = module {
    single<HttpServer> { HttpServerImplementation(get()) }
}
