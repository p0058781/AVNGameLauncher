package org.skynetsoftware.avnlauncher.jsoup

import org.jsoup.Jsoup
import org.skynetsoftware.avnlauncher.jsoup.select.Connection

actual object Jsoup {
    actual fun connect(url: String): Connection {
        return Jsoup.connect(url)
    }
}