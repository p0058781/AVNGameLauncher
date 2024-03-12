package com.myapplication

import android.app.Application
import org.skynetsoftware.avnlauncher.AVNLauncherApp

class AndroidAVNLauncherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AVNLauncherApp.onCreate()
    }
}