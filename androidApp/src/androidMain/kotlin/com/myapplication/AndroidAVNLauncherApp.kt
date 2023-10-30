package com.myapplication

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.skynetsoftware.avnlauncher.AVNLauncherApp

class AndroidAVNLauncherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AVNLauncherApp.onCreate {
            androidContext(this@AndroidAVNLauncherApp)
        }
    }
}