package org.skynetsoftware.avnlauncher.jsoup.select

import org.skynetsoftware.avnlauncher.jsoup.nodes.Document

expect interface Connection {
    fun get(): Document
}