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


object DensityUtil {
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun px2dip(context: Context, pxValue: Float):Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}
