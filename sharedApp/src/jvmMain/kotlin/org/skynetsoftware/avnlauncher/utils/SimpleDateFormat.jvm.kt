@file:Suppress("MatchingDeclarationName")

package org.skynetsoftware.avnlauncher.utils

import java.text.SimpleDateFormat

actual class SimpleDateFormat actual constructor(pattern: String) {
    private val platformSimpleDateFormat = SimpleDateFormat(pattern)

    actual fun format(date: Long): String {
        return platformSimpleDateFormat.format(date)
    }

    actual fun parse(date: String): Long? {
        return platformSimpleDateFormat.parse(date)?.time
    }
}
