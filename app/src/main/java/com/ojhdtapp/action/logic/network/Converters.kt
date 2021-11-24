package com.ojhdtapp.action.logic.network

import android.util.Log
import androidx.room.TypeConverter
import java.lang.StringBuilder
import java.util.*

class Converters {
    @TypeConverter
    fun storeDateToLong(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun getDateFromLong(long: Long?): Date? {
        return long?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun storeLabelToString(list: Map<Int, String>?): String {
        val str = StringBuilder("")
        list?.onEachIndexed { index, entry ->
            str.run {
                append(entry.key.toString())
                append("_")
                append(entry.value)
                if (index != list.size - 1) {
                    append(",")
                }
            }
        }
        return str.toString()
    }

    @TypeConverter
    fun getLabelFromString(str: String): Map<Int, String> {
        var items = str.split(",")
        val list = mutableMapOf<Int, String>()
        items.forEach {
            val labelStr = it.split("_")
            if (labelStr.size == 2) {
                labelStr.run {
                    list.put(get(0).toInt(), get(1))
                }
            }
        }
        return list
    }

    @TypeConverter
    fun storeHighlightToString(list: List<String>): String {
        var str = StringBuilder()
        list.forEachIndexed { index, s ->
            str.append(s)
            if (index != list.size - 1) {
                str.append(",")
            }
        }
//        list.forEach {
//            str.run {
//                append(it)
//                append(",")
//            }
//        }
        return str.toString()
    }

    @TypeConverter
    fun getHighlightFromString(str: String): List<String> {
        return str.split(",")
    }
}