package org.skynetsoftware.avnlauncher.link

import org.koin.core.module.Module
import java.net.URI

expect val externalLinkUtilsKoinModule: Module

interface ExternalLinkUtils {
    fun openInBrowser(uri: URI)
}
