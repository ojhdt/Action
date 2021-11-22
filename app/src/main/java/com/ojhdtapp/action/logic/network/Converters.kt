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
    fun storeLabelToString(list: List<Pair<Int, String>?>): String {
        val str = StringBuilder("")
        list.forEachIndexed { index, pair ->
            str.run {
                pair?.let {
                    append(it.first.toString())
                    append("_")
                    append(it.second)
                    if (index != list.size - 1) {
                        append(",")
                    }
                }
            }
        }
//        {
//            str.run {
//                it?.let{
//                    append(it.first.toString())
//                    append("_")
//                    append(it.second)
//                    append(",")
//                }
//            }
//        }
        return str.toString()
    }

    @TypeConverter
    fun getLabelFromString(str: String): List<Pair<Int, String>?> {
        var items = str.split(",")
        val list = mutableListOf<Pair<Int, String>?>()
        items.forEach {
            val labelStr = it.split("_")
            Log.d("aaa", labelStr.size.toString())
            if (labelStr.size == 2) {
                labelStr.run {
                    list.add(Pair(get(0).toInt(), get(1)))
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