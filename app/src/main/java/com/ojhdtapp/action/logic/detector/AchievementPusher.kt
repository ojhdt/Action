package com.ojhdtapp.action.logic.detector

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.logic.AppDataBase

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
    private val database = AppDataBase.getDataBase().actionDao()
    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getExpInformation(): ExpInformation {
        var exp = sharedPreference.getInt("exp", 0)
        var res: ExpInformation? = null
        expNeeded.forEachIndexed { index, i ->
            if (exp >= i) {
                val neededExp = if (index < 20) expNeeded[i + 1] - exp else 0
                val progress =
                    if (index < 20) ((exp - expNeeded[i]) * 100 / (expNeeded[i + 1] - expNeeded[i])).toInt() else 100
                res = ExpInformation(index, neededExp, progress)
            }
        }
        return res!!
    }

    data class ExpInformation(val levelNow: Int, val neededExp: Int, val progress: Int)

    fun tryPushingNewAchievement(){

    }
}