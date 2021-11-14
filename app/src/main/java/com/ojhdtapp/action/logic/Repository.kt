package com.ojhdtapp.action.logic

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.R
import com.ojhdtapp.action.logic.model.Achievement
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.logic.model.Suggest
import com.ojhdtapp.action.logic.model.User
import java.sql.Timestamp

object Repository {
    fun getActionNowLive() = liveData {
        val list = listOf(
            Action(
                "勤关水龙头",
                R.drawable.anonymous,
                "一些内容",
                "位置信息",
                Timestamp(1636594566),
                listOf(Pair(R.drawable.ic_outline_emoji_events_24, "WaterSave"))
            ),
            Action(
                "勤关水龙头",
                R.drawable.anonymous,
                "一些内容",
                "位置信息",
                Timestamp(1636594566),
                listOf(Pair(R.drawable.ic_outline_emoji_events_24, "WaterSave"))
            )
        )
        emit(list)
    }

    fun getSuggestMoreLive() = liveData {
        val list = listOf(
            Suggest(
                imgUrl = "https://tva2.sinaimg.cn/large/0072Vf1pgy1fodqgiodg1j31gs1191im.jpg",
                timestamp = Timestamp(1636594566),
                content = BaseApplication.context.getString(R.string.lorem_ipsum)
            )
        )
        emit(list)
    }

    fun getUserInfoLive(): LiveData<User> {
        return liveData {
            emit(User("Username", R.drawable.anonymous, Timestamp(0)))
        }
    }

    fun getGainedAchievementLive(): LiveData<List<Achievement>> {
        return liveData {
            val list = listOf(
                Achievement(),
                Achievement(),
                Achievement(),
            )
            emit(list)
        }
    }

    fun getFinishedActionLive(): LiveData<List<Action>> {
        return liveData {
            val list = listOf<Action>(
                Action(finished = true),
                Action(finished = true),
                Action(finished = true)
            )
            emit(list)
        }
    }
}