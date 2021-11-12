package com.ojhdtapp.action

import android.app.Application
import android.content.Context
import android.view.View
import androidx.core.view.WindowCompat

class BaseApplication : Application() {
    override fun onCreate() {
        context = applicationContext
        super.onCreate()
    }

    companion object {
        lateinit var context: Context

    }
}