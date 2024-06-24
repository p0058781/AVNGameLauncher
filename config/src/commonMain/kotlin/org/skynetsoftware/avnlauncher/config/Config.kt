package org.skynetsoftware.avnlauncher.config

data class Config(
    val dataDir: String,
    val cacheDir: String,
) {
    companion object Defaults {
        const val SERVER_HOST = "127.0.0.1"
        const val SERVER_PORT = 12616
        val gameThreadUrlRegex = Regex("https://f95zone.to/threads/.+\\.(\\d+).*")
    }
}
