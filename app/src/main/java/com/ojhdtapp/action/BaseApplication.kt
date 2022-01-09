package com.ojhdtapp.action

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import cn.leancloud.LeanCloud
import com.google.android.material.color.DynamicColors
import java.text.SimpleDateFormat
import java.util.*
import android.content.ContentResolver
import android.content.SharedPreferences
import android.content.res.Configuration
import android.util.Log

import androidx.annotation.AnyRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class BaseApplication : Application() {
    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onCreate() {
        context = applicationContext
        super.onCreate()

        // LeanCloud Initialize
        initLeanCLoud()

        AppCompatDelegate.setDefaultNightMode(
            when (sharedPreference.getString("dark_mode", "MODE_NIGHT_FOLLOW_SYSTEM")) {
                "MODE_NIGHT_NO" -> AppCompatDelegate.MODE_NIGHT_NO
                "MODE_NIGHT_YES" -> AppCompatDelegate.MODE_NIGHT_YES
                "MODE_NIGHT_FOLLOW_SYSTEM" -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                else -> -2
            }
        )

        // Apply Dynamic Colors
        if (sharedPreference.getBoolean("dynamic_color", DynamicColors.isDynamicColorAvailable())) {
            DynamicColors.applyToActivitiesIfAvailable(this)
        }

    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

    }
}

fun initLeanCLoud(){
    // LeanCloud Initialize
    LeanCloud.initialize(
        BaseApplication.context,
        "nRXp51Nf2KxuB3wzwcEFwgLf-gzGzoHsz",
        "ynE9lHaem1e0htqO7rQqKQsa",
        "https://api.ojhdt.com"
    )
}

fun isDarkTheme(): Boolean {
    val flag = BaseApplication.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return flag == Configuration.UI_MODE_NIGHT_YES
}

fun getUriToDrawable(
    @AnyRes drawableId: Int
): Uri {
    val context = BaseApplication.context
    return Uri.parse(
        ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.resources.getResourcePackageName(drawableId)
                + '/' + context.resources.getResourceTypeName(drawableId)
                + '/' + context.resources.getResourceEntryName(drawableId)
    )
}

interface MyOnClickListener {
    fun onClick()
}

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

object AnimType {
    const val FADE = 0
    const val HOLD = 1
    const val SHARED_AXIS_Z = 2
    const val ELEVATIONSCALE = 3
    const val NULL = 4
}