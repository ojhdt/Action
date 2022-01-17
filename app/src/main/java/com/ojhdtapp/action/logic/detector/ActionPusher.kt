package com.ojhdtapp.action.logic.detector

import android.content.SharedPreferences
import android.hardware.lights.LightState
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.logic.AppDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ActionPusher {
    companion object {
        val ACTIVITY_IN_VEHICLE = 0
        val ACTIVITY_ON_BICYCLE = 1
        val ACTIVITY_ON_FOOT = 2

        val LIGHT_FULL_MOON = 0
        val LIGHT_SUNRISE = 1
        val LIGHT_SHADE = 2
        val LIGHT_SUNLIGHT = 3

        val TIME_MIDNIGHT = 0
        val TIME_EARLY_MORNING = 1
        val TIME_MORNING = 2
        val TIME_NOON = 3
        val TIME_AFTERNOON = 4
        val TIME_DUSK = 5
        val TIME_EVENING = 6


        private val instance by lazy {
            ActionPusher()
        }

        fun getPusher(): ActionPusher {
            return instance
        }
    }

    private val context = BaseApplication.context
    private val database = AppDataBase.getDataBase().actionDao()
    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }
    private var activityState: Int = -1
    private var lightState: Int = -1
    private var timeState: Int = 0
    private var triggerTime: Long = 0

    fun submitState(activityState: Int? = null, lightState: Int? = null, timeState:Int? = null) {
        activityState?.let {
            this.activityState = it
        }
        lightState?.let {
            this.lightState = it
        }
        timeState?.let {
            this.timeState = it
        }
    }

    fun tryPushingNewAction(
        mainCause: String,
        shouldNoticeActivity: Boolean? = false,
        shouldNoticeLight: Boolean? = false,
        shouldNoticeLocation: Boolean? = false
    ) {
        if (System.currentTimeMillis() - triggerTime > 60000) {
            triggerTime = System.currentTimeMillis()
            val job = Job()
            CoroutineScope(job).launch {

            }
        }
    }
}