package org.skynetsoftware.avnlauncher.link

import org.koin.dsl.module
import java.awt.Desktop
import java.net.URI

val externalLinkUtilsKoinModule = module {
    single<ExternalLinkUtils> { ExternalLinkUtilsImpl() }
}

interface ExternalLinkUtils {
    fun openInBrowser(uri: URI)
}

private class ExternalLinkUtilsImpl : ExternalLinkUtils {
    override fun openInBrowser(uri: URI) {
        Desktop.isDesktopSupported().takeIf { it }
            .run { Desktop.getDesktop() }?.takeIf { it.isSupported(Desktop.Action.BROWSE) }?.browse(uri)
    }
}
