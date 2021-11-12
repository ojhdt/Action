package com.ojhdtapp.action

import android.app.Application
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.Window
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

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}

object DeviceUtil {
    fun getStatusBarHeight(context: Context): Int {
        // 获得状态栏高度
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
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
