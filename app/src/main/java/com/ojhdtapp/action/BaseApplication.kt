package com.ojhdtapp.action

import android.app.Application
import android.content.Context

class BaseApplication : Application() {
    override fun onCreate() {
        context = applicationContext
        super.onCreate()
    }

    companion object {
        lateinit var context: Context
    }
}