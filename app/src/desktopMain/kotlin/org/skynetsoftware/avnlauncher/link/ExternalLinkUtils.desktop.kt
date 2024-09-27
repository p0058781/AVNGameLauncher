package org.skynetsoftware.avnlauncher.link

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.domain.utils.OS
import org.skynetsoftware.avnlauncher.domain.utils.os
import org.skynetsoftware.avnlauncher.logger.Logger
import java.awt.Desktop
import java.net.URI

actual val externalLinkUtilsKoinModule = module {
    single<ExternalLinkUtils> { ExternalLinkUtilsImpl(get()) }
}

private class ExternalLinkUtilsImpl(
    private val logger: Logger,
) : ExternalLinkUtils {
    override fun openInBrowser(uri: URI) {
        val desktop = getDesktopIfSupported()
        if (desktop != null) {
            logger.debug("opening link using awt Desktop")
            desktop.browse(uri)
        } else {
            logger.debug("unable to open link, awt Desktop not supported")
            // ugly workaround for my system (sway)
            if (os == OS.Linux) {
                logger.debug("linux detected, trying to open link with xdg-open")
                xdgOpen(uri.toString())
            }
        }
    }

    private fun getDesktopIfSupported(): Desktop? {
        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                return desktop
            }
        }
        return null
    }

    private fun xdgOpen(uri: String) {
        ProcessBuilder(listOf("xdg-open", uri)).apply {
            redirectOutput(ProcessBuilder.Redirect.DISCARD)
            redirectError(ProcessBuilder.Redirect.DISCARD)
        }.start()
    }
}
