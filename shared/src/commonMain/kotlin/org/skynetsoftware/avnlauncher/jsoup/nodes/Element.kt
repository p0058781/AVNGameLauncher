package org.skynetsoftware.avnlauncher.jsoup.nodes

expect class Element {
    fun textNodes(): List<TextNode>
    fun html(): String
    fun attr(attributeKey: String): String
}