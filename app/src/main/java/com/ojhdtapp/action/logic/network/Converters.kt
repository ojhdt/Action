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
    fun storeLabelToString(list: List<Pair<Int, String>>): String {
        var str = StringBuilder("")
        list.forEach {
            str.run {
                append(it.first.toString())
                append("_")
                append(it.second)
                append(",")
            }
        }
        Log.d("aaa", str.toString())
        return str.toString()
    }

    @TypeConverter
    fun getLabelFromString(str: String): List<Pair<Int, String>> {
        var items = str.split(",")
        val list = mutableListOf<Pair<Int, String>>()
        items.forEach {
            val labelStr = it.split("_")
            labelStr.run {
                list.add(Pair(get(0).toInt(), get(1)))
            }
        }
        return list
    }

    @TypeConverter
    fun storeHighlightToString(list: List<String>): String {
        var str = StringBuilder()
        list.forEach {
            str.run {
                append(it)
                append(",")
            }
        }
        return str.toString()
    }

    @TypeConverter
    fun getHighlightFromString(str: String): List<String> {
        return str.split(",")
    }
}