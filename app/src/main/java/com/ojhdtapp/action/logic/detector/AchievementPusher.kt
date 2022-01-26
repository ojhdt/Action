package com.ojhdtapp.action.logic.detector

import android.app.Notification
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.R
import com.ojhdtapp.action.logic.AppDataBase
import com.ojhdtapp.action.logic.model.Achievement
import com.ojhdtapp.action.logic.model.Action
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class AchievementPusher {
    companion object {
        val expNeeded = arrayOf<Int>(
            0,
            50,
            100,
            200,
            300,
            420,
            540,
            660,
            800,
            950,
            1100,
            1300,
            1500,
            1700,
            1950,
            2200,
            2500,
            2800,
            3200,
            3600,
            4000
        )
        private val instance by lazy {
            AchievementPusher()
        }

        fun getPusher(): AchievementPusher {
            return instance
        }
    }

    private val context = BaseApplication.context
    private val database = AppDataBase.getDataBase()
    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getExpInformation(): ExpInformation {
        var exp = sharedPreference.getInt("exp", 0)
        var res: ExpInformation? = null
        expNeeded.forEachIndexed { index, i ->
            if (exp >= i) {
                val neededExp = if (index < 20) expNeeded[index + 1] - exp else 0
                val progress =
                    if (index < 20) ((exp - i) * 100 / (expNeeded[index + 1] - i)).toInt() else 100
                res = ExpInformation(index, neededExp, progress)
            }
        }
        return res!!
    }

    data class ExpInformation(val levelNow: Int, val neededExp: Int, val progress: Int)

    fun getActionInformation(list: List<Action>): ActionInformation {
        var totalFinished = 0
        var totalSaveWater = 0f
        var totalSaveElectricity = 0f
        var totalSaveTree = 0f
        list.forEach { action ->
            action.history.forEach {
                if (it.finished) {
                    totalSaveWater += action.canSaveWater
                    totalSaveElectricity += action.canSaveElectricity
                    totalSaveTree += action.canSaveTree
                    totalFinished++
                }
            }
        }
        return ActionInformation(totalFinished, totalSaveWater, totalSaveElectricity, totalSaveTree)
    }

    data class ActionInformation(
        val totalFinished: Int,
        val totalSaveWater: Float,
        val totalSaveElectricity: Float,
        val totalSaveTree: Float
    )

    fun tryPushingNewAchievement(specific: String? = null) {
        Log.d("aaa", "new Achievement")
        val exp = sharedPreference.getInt("exp", 0)
        specific?.let {
            val job = Job()
            CoroutineScope(job).launch {
                when (it) {
                    "sensor" -> {
                        val newAchievement = Achievement(
                            "诶？有情况！",
                            "首次触发行动",
                            context.getString(R.string.achievement_type_event),
                            R.drawable.ic_outline_directions_run_24,
                            50,
                            Date()
                        )
                        if (!database.achievementDao().isStored(newAchievement.title)){
                            database.achievementDao().insertAchievement(newAchievement)
                            sharedPreference.edit()
                                .putInt("exp", exp + 50)
                                .apply()
                        }
                    }
                    "suggestion" -> {
                        val newAchievement = Achievement(
                            "提高知识水平",
                            "阅读一条环保建议",
                            context.getString(R.string.achievement_type_schedule),
                            R.drawable.ic_outline_emoji_events_24,
                            50,
                            Date()
                        )
                        if (!database.achievementDao().isStored(newAchievement.title)){
                            database.achievementDao().insertAchievement(newAchievement)
                            sharedPreference.edit()
                                .putInt("exp", exp + 50)
                                .apply()
                        }
                    }
                    "activity" -> {
                        val newAchievement = Achievement(
                            "生命在于运动",
                            "触发身体活动事件",
                            context.getString(R.string.achievement_type_event),
                            R.drawable.ic_outline_directions_run_24,
                            50,
                            Date()
                        )
                        if (!database.achievementDao().isStored(newAchievement.title)){
                            database.achievementDao().insertAchievement(newAchievement)
                            sharedPreference.edit()
                                .putInt("exp", exp + 50)
                                .apply()
                        }
                    }
                    "accelerometer" -> {
                        val newAchievement = Achievement(
                            "别碰我！",
                            "触发重力传感器事件",
                            context.getString(R.string.achievement_type_event),
                            R.drawable.ic_outline_sensors_24,
                            50,
                            Date()
                        )
                        if (!database.achievementDao().isStored(newAchievement.title)){
                            database.achievementDao().insertAchievement(newAchievement)
                            sharedPreference.edit()
                                .putInt("exp", exp + 50)
                                .apply()
                        }
                    }
                    "light" -> {
                        val newAchievement = Achievement(
                            "天色渐暗",
                            "触发光线传感器事件",
                            context.getString(R.string.achievement_type_event),
                            R.drawable.ic_outline_sensors_24,
                            50,
                            Date()
                        )
                        if (!database.achievementDao().isStored(newAchievement.title)){
                            database.achievementDao().insertAchievement(newAchievement)
                            sharedPreference.edit()
                                .putInt("exp", exp + 50)
                                .apply()
                        }
                    }
                }
            }
            job.complete()
            return
        }
        val job = Job()
        CoroutineScope(job).launch {
            val actionInformation = getActionInformation(database.actionDao().loadAllAction())
            val level = getExpInformation().levelNow
            if (actionInformation.totalFinished == 0) {
                val newAchievement = Achievement(
                    "踏上征途",
                    "开启冒险者之旅",
                    context.getString(R.string.achievement_type_schedule),
                    R.drawable.ic_outline_emoji_events_24,
                    20,
                    Date()
                )
                if (!database.achievementDao().isStored(newAchievement.title)){
                    database.achievementDao().insertAchievement(newAchievement)
                    sharedPreference.edit()
                        .putInt("exp", exp + 20)
                        .apply()
                }
            }
            if (actionInformation.totalFinished == 1) {
                val newAchievement = Achievement(
                    "首战告捷",
                    "完成一次行动",
                    context.getString(R.string.achievement_type_schedule),
                    R.drawable.ic_outline_emoji_events_24,
                    70,
                    Date()
                )
                if (!database.achievementDao().isStored(newAchievement.title)){
                    database.achievementDao().insertAchievement(newAchievement)
                    sharedPreference.edit()
                        .putInt("exp", exp + 70)
                        .apply()
                }
            }
            if (actionInformation.totalFinished == 3) {
                val newAchievement = Achievement(
                    "三顾茅庐",
                    "完成三次行动",
                    context.getString(R.string.achievement_type_schedule),
                    R.drawable.ic_outline_emoji_events_24,
                    140,
                    Date()
                )
                if (!database.achievementDao().isStored(newAchievement.title)){
                    database.achievementDao().insertAchievement(newAchievement)
                    sharedPreference.edit()
                        .putInt("exp", exp + 140)
                        .apply()
                }
            }
            if (actionInformation.totalFinished == 10) {
                val newAchievement = Achievement(
                    "勇者出征",
                    "完成十次行动",
                    context.getString(R.string.achievement_type_schedule),
                    R.drawable.ic_outline_emoji_events_24,
                    250,
                    Date()
                )
                if (!database.achievementDao().isStored(newAchievement.title)){
                    database.achievementDao().insertAchievement(newAchievement)
                    sharedPreference.edit()
                        .putInt("exp", exp + 250)
                        .apply()
                }
            }
            if (actionInformation.totalFinished == 50) {
                val newAchievement = Achievement(
                    "你是一位英雄！",
                    "完成五十次行动",
                    context.getString(R.string.achievement_type_schedule),
                    R.drawable.ic_outline_emoji_events_24,
                    800,
                    Date()
                )
                if (!database.achievementDao().isStored(newAchievement.title)){
                    database.achievementDao().insertAchievement(newAchievement)
                    sharedPreference.edit()
                        .putInt("exp", exp + 800)
                        .apply()
                }
            }

            if (actionInformation.totalSaveTree > 1f) {
                val newAchievement = Achievement(
                    "护林员",
                    "累计保护一株成年树木",
                    context.getString(R.string.achievement_type_resource),
                    R.drawable.ic_outline_forest_24,
                    200,
                    Date()
                )
                if (!database.achievementDao().isStored(newAchievement.title)){
                    database.achievementDao().insertAchievement(newAchievement)
                    sharedPreference.edit()
                        .putInt("exp", exp + 200)
                        .apply()
                }
            }
            if (actionInformation.totalSaveTree > 10f) {
                val newAchievement = Achievement(
                    "节水主义者",
                    "累计节约用水10立方米",
                    context.getString(R.string.achievement_type_resource),
                    R.drawable.ic_outline_water_drop_24,
                    200,
                    Date()
                )
                if (!database.achievementDao().isStored(newAchievement.title)){
                    database.achievementDao().insertAchievement(newAchievement)
                    sharedPreference.edit()
                        .putInt("exp", exp + 200)
                        .apply()
                }
            }
            if (actionInformation.totalSaveTree > 10f) {
                val newAchievement = Achievement(
                    "节水主义者",
                    "累计节约用水10立方米",
                    context.getString(R.string.achievement_type_resource),
                    R.drawable.ic_outline_water_drop_24,
                    200,
                    Date()
                )
                if (!database.achievementDao().isStored(newAchievement.title)){
                    database.achievementDao().insertAchievement(newAchievement)
                    sharedPreference.edit()
                        .putInt("exp", exp + 200)
                        .apply()
                }
            }
            if (actionInformation.totalSaveElectricity > 10f) {
                val newAchievement = Achievement(
                    "节电主义者",
                    "累计节约用电10千瓦时",
                    context.getString(R.string.achievement_type_resource),
                    R.drawable.ic_outline_bolt_24,
                    200,
                    Date()
                )
                if (!database.achievementDao().isStored(newAchievement.title)){
                    database.achievementDao().insertAchievement(newAchievement)
                    sharedPreference.edit()
                        .putInt("exp", exp + 200)
                        .apply()
                }
            }
        }
        job.complete()
    }
}