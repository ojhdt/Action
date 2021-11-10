package com.ojhdtapp.action.logic

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.ojhdtapp.action.R
import com.ojhdtapp.action.logic.model.Action

object Repository {
    fun getActionNowLive() = liveData {
        val list = listOf(
            Action(
                "勤关水龙头",
                R.drawable.anonymous,
                "一些内容",
                listOf(Pair(R.drawable.ic_outline_emoji_events_24, "WaterSave"))
            ),
            Action(
                "勤关水龙头",
                R.drawable.anonymous,
                "一些内容",
                listOf(Pair(R.drawable.ic_outline_emoji_events_24, "WaterSave"))
            )
        )
        emit(list)
    }

}