package com.ojhdtapp.action.util

import android.content.Context

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
}