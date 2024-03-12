package org.skynetsoftware.avnlauncher.data

import kotlinx.coroutines.*
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.data.repository.GamesRepository
import org.skynetsoftware.avnlauncher.f95.createF95ThreadUrl
import org.skynetsoftware.avnlauncher.jsoup.Jsoup

val gameImportKoinModule = module {
    single<GameImport> { GameImportImpl(get(), get()) }
}

interface GameImport {
    fun importGame(threadId: Int, onGameImported: (title: String) -> Unit): Job
}

private class GameImportImpl(configManager: ConfigManager, private val gamesRepository: GamesRepository) : GameImport {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val titleRegex = Regex("(.+)\\s*\\[(.+)\\]\\s*\\[(.+)\\]")
    //private val gamesDir = configManager.gamesDir

    override fun importGame(threadId: Int, onGameImported: (title: String) -> Unit) = coroutineScope.launch {
        //TODO use F95Api class
        val document = Jsoup.connect(threadId.createF95ThreadUrl()).get()
        val titleRaw = document.select(".p-title-value").first()?.textNodes()?.first()?.text()
            ?: throw IllegalArgumentException("cant get title")
        val imageUrl = document.select(".bbWrapper div a").first()?.attr("href")
            ?: throw IllegalArgumentException("invalid imageUrl detected")

        val matchResult = titleRegex.matchEntire(titleRaw)
        val title =
            matchResult?.groups?.get(1)?.value?.trim() ?: throw IllegalArgumentException("invalid title detected")
        val version = matchResult.groups[2]?.value?.trim() ?: throw IllegalArgumentException("invalid version detected")

        //TODO search executable/android app
        /*val executable = gamesDir.listFiles()
            ?.filter { it.isDirectory }
            ?.firstOrNull { it.name.lowercase().contains(title.replace(" ", "").lowercase()) }?.let {
                findExecutable(it.absolutePath)
            }?.absolutePath ?: ""
*/
        gamesRepository.insertGame(title, imageUrl, threadId, null, version)
        onGameImported(title)
    }

    /*private fun findExecutable(searchDirectory: String): File? {
        Files.walk(Paths.get(searchDirectory)).use { files ->
            return files
                .filter { f -> f.extension == "sh" }
                .findFirst().getOrNull()?.toFile()
        }
    }*/
}