package org.skynetsoftware.avnlauncher.link

import org.koin.dsl.module
import java.awt.Desktop
import java.net.URI

actual val externalLinkUtilsKoinModule = module {
    single<ExternalLinkUtils> { ExternalLinkUtilsImpl() }
}

private class ExternalLinkUtilsImpl : ExternalLinkUtils {
    override fun openInBrowser(uri: URI) {
        Desktop.isDesktopSupported().takeIf { it }
            .run { Desktop.getDesktop() }?.takeIf { it.isSupported(Desktop.Action.BROWSE) }?.browse(uri)
    }
}
