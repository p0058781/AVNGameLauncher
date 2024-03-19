package org.skynetsoftware.avnlauncher.utils

import okio.FileSystem
import okio.Path

fun Path.readToString() =
    buildString {
        FileSystem.SYSTEM.read(this@readToString) {
            while (true) {
                val line = readUtf8Line() ?: break
                appendLine(line)
            }
        }
    }
