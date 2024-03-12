package org.skynetsoftware.avnlauncher.jsoup

import org.skynetsoftware.avnlauncher.jsoup.select.Connection

expect object Jsoup {
    fun connect(url: String): Connection
}