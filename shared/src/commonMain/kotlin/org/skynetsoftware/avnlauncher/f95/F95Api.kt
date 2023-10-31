package org.skynetsoftware.avnlauncher.f95

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.f95.model.F95Game
import org.skynetsoftware.avnlauncher.jsoup.Jsoup

private const val f95ZoneThreadBaseUrl = "https://f95zone.to/threads/"

val f95ApiKoinModule = module {
    single<F95Api> { F95ApiImpl() }
}

fun Int.createF95ThreadUrl() = "$f95ZoneThreadBaseUrl$this"

interface F95Api {
    suspend fun getGame(gameThreadId: Int): Result<F95Game>
    suspend fun getGame(gameThreadUrl: String): Result<F95Game>
}

private class F95ApiImpl : F95Api {

    private val titleRegex = Regex("(.+)\\s*\\[(.+)\\]\\s*\\[(.+)\\]")
    private val releaseDateRegex = Regex("<b>Release Date</b>: (.*)")

    override suspend fun getGame(gameThreadId: Int): Result<F95Game> {
        return try {
            val document = Jsoup.connect("$f95ZoneThreadBaseUrl$gameThreadId").get()
            val titleRaw = document.select(".p-title-value").first()?.textNodes()?.first()?.text()
                ?: throw IllegalArgumentException("cant get title")
            val imageUrl = document.select(".bbWrapper div a").first()?.attr("href")
                ?: throw IllegalArgumentException("invalid imageUrl detected")
            val bbWrapper = document.select(".bbWrapper").first()?.html()
                ?: throw IllegalArgumentException(".bbWrapper")

            val matchResult = titleRegex.matchEntire(titleRaw)
            val title =
                matchResult?.groups?.get(1)?.value?.trim() ?: throw IllegalArgumentException("invalid title detected")
            val version =
                matchResult.groups[2]?.value?.trim() ?: throw IllegalArgumentException("invalid version detected")
            val releaseDate = releaseDateRegex.find(bbWrapper)?.groups?.get(1)?.value
                ?: throw IllegalArgumentException("can't get release data")

            val rating = TODO()
            val firstReleaseDate = TODO()
            val tags = TODO()
            val game = F95Game(gameThreadId, title, imageUrl, version, rating, firstReleaseDate, releaseDate, tags)
            return Result.success(game)
        } catch (t: Throwable) {
            Result.failure<F95Game>(t)
        }
    }

    override suspend fun getGame(gameThreadUrl: String): Result<F95Game> {
        TODO()
    }

}