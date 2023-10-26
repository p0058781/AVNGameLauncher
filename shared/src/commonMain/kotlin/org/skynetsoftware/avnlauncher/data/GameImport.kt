package org.skynetsoftware.avnlauncher.data

import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.extension
import kotlin.jvm.optionals.getOrNull

val gameImportKoinModule = module {
    single<GameImport> { GameImportImpl(get(), get()) }
}

interface GameImport {
    fun importGame(url: String, onGameImported: (title: String) -> Unit): Job
}

private class GameImportImpl(configManager: ConfigManager, private val gamesRepository: GamesRepository) : GameImport {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val titleRegex = Regex("(.+)\\s*\\[(.+)\\]\\s*\\[(.+)\\]")
    private val gamesDir = configManager.gamesDir

    override fun importGame(url: String, onGameImported: (title: String) -> Unit) = coroutineScope.launch {
        val document = Jsoup.connect(url).get()
        val titleRaw = document.select(".p-title-value").first()?.textNodes()?.first()?.text()
            ?: throw IllegalArgumentException("cant get title")
        val imageUrl = document.select(".bbWrapper div a").first()?.attr("href")
            ?: throw IllegalArgumentException("invalid imageUrl detected")

        val matchResult = titleRegex.matchEntire(titleRaw)
        val title =
            matchResult?.groups?.get(1)?.value?.trim() ?: throw IllegalArgumentException("invalid title detected")
        val version = matchResult.groups[2]?.value?.trim() ?: throw IllegalArgumentException("invalid version detected")

        val executable = gamesDir.listFiles()
            ?.filter { it.isDirectory }
            ?.firstOrNull { it.name.lowercase().contains(title.replace(" ", "").lowercase()) }?.let {
                findExecutable(it.absolutePath)
            }?.absolutePath ?: ""

        gamesRepository.insertGame(title, imageUrl, url, executable, version, System.currentTimeMillis())
        onGameImported(title)
    }

    private fun findExecutable(searchDirectory: String): File? {
        Files.walk(Paths.get(searchDirectory)).use { files ->
            return files
                .filter { f -> f.extension == "sh" }
                .findFirst().getOrNull()?.toFile()
        }
    }
}