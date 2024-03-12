package org.skynetsoftware.avnlauncher.data.f95

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import org.jsoup.Jsoup
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.f95.model.F95Game
import org.skynetsoftware.avnlauncher.domain.utils.Result
import java.text.SimpleDateFormat

internal actual fun Module.f95ParserKoinModule() {
    single<F95Parser> { F95ParserJsoup() }
}

private class F95ParserJsoup : F95Parser {
    private val titleRegex = Regex("(.+)\\s*\\[(.+)\\]\\s*\\[(.+)\\]")
    private val releaseDateRegex = Regex("<b>Release Date.*:.+?(\\d{4}-\\d{2}-\\d{2})")
    private val ratingRegex = Regex("[+-]?([0-9]*[.])?[0-9]+")
    private val releaseDateFormat = SimpleDateFormat("yyyy-MM-dd")

    override suspend fun parseGame(
        httpResponse: HttpResponse,
        gameThreadId: Int,
    ): Result<F95Game> {
        return try {
            val document = Jsoup.parse(httpResponse.bodyAsText())
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
            val releaseDate = releaseDateFormat.parse(
                releaseDateRegex.find(
                    bbWrapper.first()?.html() ?: throw IllegalArgumentException("bbWraper html not found"),
                )?.groups?.get(1)?.value
                    ?: throw IllegalArgumentException("can't get release data"),
            ) ?: throw IllegalArgumentException("can't parse releaseDate")

            val ratingRaw =
                document.select("div.p-title-pageAction:nth-child(1) > span:nth-child(1) > span:nth-child(1)").first()
                    ?.attr("title")
                    ?: throw IllegalArgumentException("cant get rating")
            val rating = ratingRegex.find(ratingRaw)?.groups?.get(0)?.value?.trim()?.toFloatOrNull()
                ?: throw IllegalArgumentException("cant parse rating")
            val firstReleaseDate =
                (
                    document.select(".listInline > li:nth-child(2) > a:nth-child(3) > time:nth-child(1)").first()
                        ?.attr("data-time")?.toLongOrNull()
                        ?: throw IllegalArgumentException("cant parse firstReleaseDate")
                ) * 1000L
            val tagsContainer = document.select(".js-tagList a.tagItem")
            val tags = hashSetOf<String>()
            tagsContainer.forEach {
                tags.add(it.text())
            }
            val game = F95Game(gameThreadId, title, imageUrl, version, rating, firstReleaseDate, releaseDate.time, tags)
            Result.Ok(game)
        } catch (t: Throwable) {
            Result.Error(t)
        }
    }
}
