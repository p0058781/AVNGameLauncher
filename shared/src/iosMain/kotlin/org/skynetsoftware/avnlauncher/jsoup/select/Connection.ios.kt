package org.skynetsoftware.avnlauncher.jsoup.select

import org.skynetsoftware.avnlauncher.jsoup.nodes.Document

actual interface Connection {
    actual fun get(): Document
}