package com.ojhdtapp.action.util

import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.R
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    fun formatDate(date: Date): String {
        val format = SimpleDateFormat(BaseApplication.context.getString(R.string.date_format))
        return format.format(date)
    }

    fun formatDateWithoutHM(date: Date): String {
        val format =
            SimpleDateFormat(BaseApplication.context.getString(R.string.date_format_without_h_m))
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