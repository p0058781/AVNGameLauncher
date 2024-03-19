package org.skynetsoftware.avnlauncher.link

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.net.URI

actual val externalLinkUtilsKoinModule = module {
    single<ExternalLinkUtils> { ExternalLinkUtilsAndroid(androidContext()) }
}

private class ExternalLinkUtilsAndroid(private val context: Context) : ExternalLinkUtils {
    override fun openInBrowser(uri: URI) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
