package com.ojhdtapp.action.logic.network

import androidx.room.TypeConverter
import com.ojhdtapp.action.logic.model.ActionHistory
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

    @TypeConverter
    fun storeHistoryToString(list: List<ActionHistory>): String {
        var str = StringBuilder()
        var historyStr = StringBuilder()
        list.forEachIndexed { index, actionHistory ->
            historyStr.run {
                append(storeDateToLong(actionHistory.time).toString())
                append(";")
                append(actionHistory.cause)
                append(";")
                append(actionHistory.finished.toString())
            }
            str.append(historyStr.toString())
            if (index != list.size - 1) {
                str.append(",")
            }
        }
        return str.toString()
    }

    @TypeConverter
    fun getHistoryFromString(str:String):List<ActionHistory>{
        val tempList = str.split(",")
        var resList = mutableListOf<ActionHistory>()
        tempList.forEach {
            val tempHistoryList = it.split(";")
            val history = if(tempHistoryList.size == 3){
                ActionHistory(getDateFromLong(tempHistoryList[0].toLong())!!,
                tempHistoryList[1],
                tempHistoryList[2].toBooleanStrict())
            } else null
            if (history != null) {
                resList.add(history)
            }
        }
        return resList
    }
}