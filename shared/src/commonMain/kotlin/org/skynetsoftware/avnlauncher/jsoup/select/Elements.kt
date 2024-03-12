package org.skynetsoftware.avnlauncher.jsoup.select

import org.skynetsoftware.avnlauncher.jsoup.nodes.Element

expect class Elements {
    fun first(): Element?

    // filters
    fun select(query: String): Elements
}