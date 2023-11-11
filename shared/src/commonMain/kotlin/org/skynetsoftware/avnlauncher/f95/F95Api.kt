package org.skynetsoftware.avnlauncher.f95

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.model.Game
import org.skynetsoftware.avnlauncher.f95.model.F95Game
import org.skynetsoftware.avnlauncher.jsoup.Jsoup
import org.skynetsoftware.avnlauncher.utils.SimpleDateFormat

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
    private val ratingRegex = Regex("[+-]?([0-9]*[.])?[0-9]+")
    private val releaseDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val firstReleaseDateFormat = SimpleDateFormat("MMM dd, yyyy")

    override suspend fun getGame(gameThreadId: Int): Result<F95Game> {
        return try {
            val document = Jsoup.connect("$f95ZoneThreadBaseUrl$gameThreadId").get()
            val bbWrapper = document.select(".bbWrapper")
            val imageUrl = bbWrapper.select("div a").first()?.attr("href")
                ?: throw IllegalArgumentException("invalid imageUrl detected")
            val titleRaw = document.select(".p-title-value").first()?.textNodes()?.first()?.text()
                ?: throw IllegalArgumentException("cant get title")

            val matchResult = titleRegex.matchEntire(titleRaw)
            val title =
                matchResult?.groups?.get(1)?.value?.trim() ?: throw IllegalArgumentException("invalid title detected")
            val version =
                matchResult.groups[2]?.value?.trim() ?: throw IllegalArgumentException("invalid version detected")
            val releaseDate = releaseDateFormat.parse(releaseDateRegex.find(bbWrapper.first()?.html() ?: throw IllegalArgumentException("bbWraper html not found"))?.groups?.get(1)?.value
                ?: throw IllegalArgumentException("can't get release data"))

            val ratingRaw = document.select("div.p-title-pageAction:nth-child(1) > span:nth-child(1) > span:nth-child(1)").first()?.attr("title")
                ?: throw IllegalArgumentException("cant get rating")
            val rating = ratingRegex.find(ratingRaw)?.groups?.get(0)?.value?.trim()?.toFloatOrNull() ?: throw IllegalArgumentException("cant parse rating")
            val firstReleaseDate = document.select(".listInline > li:nth-child(2) > a:nth-child(3) > time:nth-child(1)").first()?.attr("data-time")?.toLongOrNull()
                ?: throw IllegalArgumentException("cant parse firstReleaseDate")
            val tagsContainer = document.select(".js-tagList a.tagItem")
            val tags = TODO("[medium] parse tags")
            val game = F95Game(gameThreadId, title, imageUrl, version, rating, firstReleaseDate, releaseDate, tags)
            return Result.success(game)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun getGame(gameThreadUrl: String): Result<F95Game> {
        TODO("[medium] parse threadId from url and call getGame(threadId)")
    }

}