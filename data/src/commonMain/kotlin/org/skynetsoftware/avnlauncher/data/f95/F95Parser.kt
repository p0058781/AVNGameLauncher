package org.skynetsoftware.avnlauncher.data.f95

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.f95.model.F95Game
import org.skynetsoftware.avnlauncher.domain.utils.Result
import org.skynetsoftware.avnlauncher.logger.Logger
import java.text.SimpleDateFormat

private const val ONE_SECOND_MILLIS = 1000L

internal fun Module.f95ParserKoinModule() {
    single<F95Parser> { F95ParserJsoup(get()) }
}

internal interface F95Parser {
    suspend fun parseGame(
        httpResponse: HttpResponse,
        gameThreadId: Int,
    ): Result<F95Game>
}

private class F95ParserJsoup(
    private val logger: Logger,
) : F95Parser {
    private val titleRegex = Regex("(.+)\\s*\\[(.+)\\]\\s*\\[(.+)\\]")
    private val releaseDateRegex = Regex("<b>Release Date.*:.+?(\\d{4}-\\d{2}-\\d{2})")
    private val ratingRegex = Regex("[+-]?([0-9]*[.])?[0-9]+")
    private val releaseDateFormat = SimpleDateFormat("yyyy-MM-dd")

    @Suppress("TooGenericExceptionCaught")
    override suspend fun parseGame(
        httpResponse: HttpResponse,
        gameThreadId: Int,
    ): Result<F95Game> {
        if (httpResponse.status != HttpStatusCode.OK) {
            return Result.Error(Exception("http status code: ${httpResponse.status.value}"))
        }
        return try {
            val document = Jsoup.parse(httpResponse.bodyAsText())
            val imageUrl = parseImageUrl(document)
            val (title, version) = parseTitleVersion(document)
            val releaseDate = parseReleaseDate(document)
            val rating = parseRating(document)
            val firstReleaseDate = parseFirstReleaseDate(document)
            val tags = parseTags(document)
            val game = F95Game(gameThreadId, title, imageUrl, version, rating, firstReleaseDate, releaseDate, tags)
            Result.Ok(game)
        } catch (t: Throwable) {
            logger.error(t)
            Result.Error(t)
        }
    }

    private fun parseTags(document: Document): Set<String> {
        val tagsContainer = document.select(".js-tagList a.tagItem")
        val tags = hashSetOf<String>()
        tagsContainer.forEach {
            tags.add(it.text())
        }
        return tags
    }

    @Throws(IllegalStateException::class)
    private fun parseFirstReleaseDate(document: Document): Long {
        return (
            document.select(".listInline > li:nth-child(2) > a:nth-child(3) > time:nth-child(1)").first()
                ?.attr("data-time")?.toLongOrNull()
                ?: error("cant parse firstReleaseDate")
        ) * ONE_SECOND_MILLIS
    }

    @Throws(IllegalStateException::class)
    private fun parseRating(document: Document): Float {
        val rating =
            document.select("div.p-title-pageAction:nth-child(1) > span:nth-child(1) > span:nth-child(1)")
                .first()?.attr("title")?.let {
                    ratingRegex.find(it)?.groups?.get(0)?.value?.trim()?.toFloatOrNull()
                }
        if (rating == null) {
            logger.warning("can't get rating")
            return 0f
        }
        return rating
    }

    @Throws(IllegalStateException::class)
    private fun parseReleaseDate(document: Document): Long {
        return releaseDateFormat.parse(
            releaseDateRegex.find(
                document.select(".bbWrapper").first()?.html() ?: error("bbWraper html not found"),
            )?.groups?.get(1)?.value
                ?: error("can't get release data"),
        )?.time ?: error("can't parse releaseDate")
    }

    @Throws(IllegalStateException::class)
    private fun parseTitleVersion(document: Document): Pair<String, String> {
        val titleRaw = document.select(".p-title-value").first()?.textNodes()?.first()?.text()
            ?: error("cant get title")
        val matchResult = titleRegex.matchEntire(titleRaw)
        val title =
            matchResult?.groups?.get(1)?.value?.trim() ?: error("invalid title detected")
        val version =
            matchResult.groups[2]?.value?.trim() ?: error("invalid version detected")
        return title to version
    }

    @Throws(IllegalStateException::class)
    private fun parseImageUrl(document: Document): String {
        val bbImage = document.select("img.bbImage").first()
        return if (bbImage?.parent()?.`is`("a") == true) {
            bbImage.parent()?.attr("href")
        } else {
            bbImage?.attr("src")
        } ?: error("failed to parse imageUrl")
    }
}
