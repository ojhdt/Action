package com.ojhdtapp.action

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import cn.leancloud.LCObject
import cn.leancloud.LeanCloud
import com.google.android.material.color.DynamicColors
import java.text.SimpleDateFormat
import java.util.*


class BaseApplication : Application() {
    override fun onCreate() {
        context = applicationContext
        super.onCreate()

        // LeanCloud Initialize
        LeanCloud.initialize(
            this,
            "nRXp51Nf2KxuB3wzwcEFwgLf-gzGzoHsz",
            "ynE9lHaem1e0htqO7rQqKQsa",
            "https://api.ojhdt.com"
        )

        // Apply Dynamic Colors
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

    }
}

interface MyOnClickListener{
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
    val FADE = 0
    val HOLD = 1
    val SHARED_AXIS_Z = 2
    val ELEVATIONSCALE = 3
    val NULL = 4
}

object DateUtil {
    fun formatDate(date: Date): String {
        val format = SimpleDateFormat(BaseApplication.context.getString(R.string.date_format))
        return format.format(date)
    }

    fun formatDateWithoutHM(date: Date): String {
        val format = SimpleDateFormat(BaseApplication.context.getString(R.string.date_format_without_h_m))
        return format.format(date)
    }

    fun formatDateForDetail(date: Date): List<String> {
        val yearFormat = SimpleDateFormat(BaseApplication.context.getString(R.string.year_format))
        val monthFormat = SimpleDateFormat(BaseApplication.context.getString(R.string.month_format))
        val dayFormat = SimpleDateFormat(BaseApplication.context.getString(R.string.day_format))
        val timeFormat = SimpleDateFormat(BaseApplication.context.getString(R.string.time_format))
        return listOf(
            yearFormat.format(date),
            monthFormat.format(date),
            dayFormat.format(date),
            timeFormat.format(date)
        )
    }

    fun timeAgo(createdTime: Date?): String? {
        val format = SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)
        return if (createdTime != null) {
            val agoTimeInMin =
                (Date(System.currentTimeMillis()).time - createdTime.time) / 1000 / 60
            //如果在当前时间以前一分钟内
            if (agoTimeInMin <= 1) {
                "刚刚"
            } else if (agoTimeInMin <= 60) {
                //如果传入的参数时间在当前时间以前10分钟之内
                agoTimeInMin.toString() + "分钟前"
            } else if (agoTimeInMin <= 60 * 24) {
                (agoTimeInMin / 60).toString() + "小时前"
            } else if (agoTimeInMin <= 60 * 24 * 2) {
                (agoTimeInMin / (60 * 24)).toString() + "天前"
            } else {
                format.format(createdTime)
            }
        } else {
            format.format(Date(0))
        }
    }
}


object DensityUtil {
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}

object DeviceUtil {
    // 获得状态栏高度
    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }
    // 获得导航栏高度
    fun getNavigationBarHeight(context: Context): Int {
        val rid: Int =
            context.resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return if (rid != 0) {
            val resourceId: Int =
                context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }
//    private fun getStatusBarHeight(window: Window, context: Context): Int {
//        val localRect = Rect()
//        window.decorView.getWindowVisibleDisplayFrame(localRect)
//        var mStatusBarHeight = localRect.top
//        if (0 == mStatusBarHeight) {
//            try {
//                val localClass = Class.forName("com.android.internal.R\$dimen")
//                val localObject = localClass.newInstance()
//                val i5 =
//                    localClass.getField("status_bar_height")[localObject].toString().toInt()
//                mStatusBarHeight = context.resources.getDimensionPixelSize(i5)
//            } catch (var6: ClassNotFoundException) {
//                var6.printStackTrace()
//            } catch (var7: IllegalAccessException) {
//                var7.printStackTrace()
//            } catch (var8: InstantiationException) {
//                var8.printStackTrace()
//            } catch (var9: NumberFormatException) {
//                var9.printStackTrace()
//            } catch (var10: IllegalArgumentException) {
//                var10.printStackTrace()
//            } catch (var11: SecurityException) {
//                var11.printStackTrace()
//            } catch (var12: NoSuchFieldException) {
//                var12.printStackTrace()
//            }
//        }
//        if (0 == mStatusBarHeight) {
//            val resourceId: Int =
//                context.resources.getIdentifier("status_bar_height", "dimen", "android")
//            if (resourceId > 0) {
//                mStatusBarHeight = context.resources.getDimensionPixelSize(resourceId)
//            }
//        }
//        return mStatusBarHeight
//    }
}
