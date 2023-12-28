package org.skynetsoftware.avnlauncher.utils

val osName = System.getProperty("os.name").lowercase()

val os = when {
    osName.contains("win") -> {
        OS.Windows
    }
    osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> {
        OS.Linux
    }
    osName.contains("mac") -> {
        OS.Mac
    }
    else -> throw IllegalStateException("Operating System: '$osName' is not supported\nPlease contact developer.")
}

enum class OS {
    Linux,
    Windows,
    Mac,
}
