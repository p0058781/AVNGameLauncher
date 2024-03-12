package org.skynetsoftware.avnlauncher.jsoup.nodes

import org.skynetsoftware.avnlauncher.jsoup.select.Elements

expect class Document {
    fun select(cssQuery: String): Elements
}