package org.skynetsoftware.avnlauncher.f95

import com.russhwolf.settings.Settings
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.CookieEncoding
import io.ktor.http.Url
import io.ktor.util.date.GMTDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class F95CookiesStorage(private val settings: Settings) : CookiesStorage {
    private val cookies: MutableList<Cookie> = mutableListOf()

    init {
        readFromSetting()
    }

    override suspend fun addCookie(
        requestUrl: Url,
        cookie: Cookie,
    ) {
        if (requestUrl.host != "f95zone.to") return
        if (cookie.name.isBlank()) return

        cookies.removeAll { it.name == cookie.name }
        cookies.add(cookie)
        persistToSettings()
    }

    override suspend fun get(requestUrl: Url): List<Cookie> {
        if (requestUrl.host != "f95zone.to") {
            return emptyList()
        }

        return cookies
    }

    override fun close() {}

    private fun persistToSettings() {
        val serializableCookies = cookies.map { it.toSerializableCookie() }
        val encoded = Json.encodeToString(serializableCookies)
        settings.putString("cookies", encoded)
    }

    private fun readFromSetting() {
        val encoded = settings.getStringOrNull("cookies")
        encoded?.let {
            val serializableCookies = Json.decodeFromString<List<SerializableCookie>>(it)
            cookies.clear()
            cookies.addAll(serializableCookies.map { it.toCookie() })
        }
    }
}

@Serializable
private data class SerializableCookie(
    val name: String,
    val value: String,
    val encoding: String,
    val maxAge: Int,
    val expires: Long?,
    val secure: Boolean,
    val httpOnly: Boolean,
    val extensions: Map<String, String?>,
)

private fun Cookie.toSerializableCookie(): SerializableCookie {
    return SerializableCookie(
        name = name,
        value = value,
        encoding = encoding.toString(),
        maxAge = maxAge,
        expires = expires?.timestamp,
        secure = secure,
        httpOnly = httpOnly,
        extensions = extensions,
    )
}

private fun SerializableCookie.toCookie(): Cookie {
    return Cookie(
        name = name,
        value = value,
        encoding = CookieEncoding.valueOf(encoding),
        maxAge = maxAge,
        expires = GMTDate(expires),
        secure = secure,
        httpOnly = httpOnly,
        extensions = extensions,
    )
}
