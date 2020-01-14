package com.kinvey.sample.micflow

import android.app.Application

class App : Application() {

    private var instance: App? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}