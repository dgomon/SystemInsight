package com.dgomon.systeminsight

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SystemInsightApplication : Application() {
    // You can add application-wide setup here if needed
    override fun onCreate() {
        super.onCreate()
        // ...
    }
}