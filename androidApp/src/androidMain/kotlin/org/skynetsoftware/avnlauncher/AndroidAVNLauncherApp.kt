package org.skynetsoftware.avnlauncher

import android.app.Application
import org.koin.android.ext.koin.androidContext

class AndroidAVNLauncherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AVNLauncherApp.onCreate {
            androidContext(this@AndroidAVNLauncherApp)
        }
    }
}
