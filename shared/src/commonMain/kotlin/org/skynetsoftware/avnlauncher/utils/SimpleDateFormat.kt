package org.skynetsoftware.avnlauncher.utils

expect class SimpleDateFormat(pattern: String) {
    fun format(date: Long): String

    fun parse(date: String): Long?
}
