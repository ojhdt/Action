package com.ojhdtapp.action.logic.detector

import android.content.SharedPreferences
import android.hardware.lights.LightState
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.R
import com.ojhdtapp.action.logic.AppDataBase
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.logic.model.ActionHistory
import com.ojhdtapp.action.util.NotificationUtil
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

        val STATE_ACTIVITY = 0
        val STATE_LIGHT = 1
        val STATE_LOCATION = 2
        val STATE_TIME = 3
        val STATE_WEATHER = 4

        val stateList = listOf<MutableList<Boolean>>(
            mutableListOf(true, true, true, true),
            mutableListOf(true, true, true, false),
            mutableListOf(true, true, false, true),
            mutableListOf(true, false, true, true),
            mutableListOf(false, true, true, true),
            mutableListOf(true, true, false, false),
            mutableListOf(true, false, true, false),
            mutableListOf(false, true, true, false),
            mutableListOf(true, false, false, true),
            mutableListOf(false, true, false, true),
            mutableListOf(false, false, true, true),
            mutableListOf(true, false, false, false),
            mutableListOf(false, true, false, false),
            mutableListOf(false, false, true, false),
            mutableListOf(false, false, false, true),
            mutableListOf(false, false, false, false),
        )

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
    private var locationState: Int = -1
    private var timeState: Int = -1
    private var weatherState: Int = -1
    private var triggerTime: Long = 0

    fun submitState(activityState: Int? = null, lightState: Int? = null, timeState: Int? = null) {
        activityState?.let {
            this.activityState = it
        }
        lightState?.let {
            this.lightState = it
        }
        timeState?.let {
            this.timeState = it
        }
        Log.d(
            "sensor",
            this.activityState.toString() + " " + this.lightState.toString() + " " + this.timeState.toString()
        )
    }

    fun tryPushingNewAction(
        mainTrigger: String,
//        shouldNoticeActivity: Boolean? = false,
//        shouldNoticeLight: Boolean? = false,
//        shouldNoticeLocation: Boolean? = false
    ) {
        Log.d("sensor", (System.currentTimeMillis() - triggerTime).toString())
        if (System.currentTimeMillis() - triggerTime > 60000) {
            triggerTime = System.currentTimeMillis()
            val job = Job()
            CoroutineScope(job).launch {
                var tempList: List<Action> = listOf()
                when (mainTrigger) {
                    "activity" -> {
                        possibleForeach(0, mainTrigger)
                    }
                    "light" -> {
                        possibleForeach(1, mainTrigger)
                    }
                    "location" -> {
                        possibleForeach(2, mainTrigger)
                    }
                    "time" -> {
                        possibleForeach(3, mainTrigger)
                    }
                    "weather" -> {
                        possibleForeach(4, mainTrigger)
                    }
                    else -> {
                        possibleForeach(-1, mainTrigger)
                    }
                }
            }
        }
    }

    private suspend fun possibleForeach(target: Int, mainTrigger: String? = null) {
        var tempList: List<Action>
        if (target == -1) {
            tempList = Repository.loadAvailableActionByConditions(-1, -1, -1, -1, -1)
            if (tempList.isNotEmpty()) {
                editAndPushAction(
                    selectActionRandomlyByWeight(tempList),
                    false, false, false, false, false, mainTrigger
                )
                return
            }
        } else {
            stateList.forEachIndexed { index, booleans ->
                booleans.add(target, true)
                tempList = database.loadAvailableActionByConditions(
                    if (booleans[0]) activityState else -1,
                    if (booleans[1]) lightState else -1,
                    if (booleans[2]) locationState else -1,
                    if (booleans[3]) timeState else -1,
                    if (booleans[4]) weatherState else -1
                )
                if (tempList.isNotEmpty()) {
                    editAndPushAction(
                        selectActionRandomlyByWeight(tempList),
                        booleans[0], booleans[1], booleans[2], booleans[3], booleans[4], mainTrigger
                    )
                    return
                }
            }
        }
        Log.d("aaa", "Nothing can be push")
    }

    private fun selectActionRandomlyByWeight(list: List<Action>): Action {
        var sum = 0
        var ssum = 0
        list.forEach {
            sum += it.weight
        }
        var ranNum = (0..sum).random()
        list.forEach {
            ssum += it.weight
            if (ssum >= ranNum) {
                return it
            }
        }
        Log.d("aaa", "Random Failed")
        return list.random()
    }

    private fun editAndPushAction(
        item: Action,
        isActivityStateUsed: Boolean = false,
        isLightStateUsed: Boolean = false,
        isLocationStateUsed: Boolean = false,
        isTimeStateUsed: Boolean = false,
        isWeatherStateUsed: Boolean = false,
        mainTrigger: String? = null
    ) {
        val causeStr = StringBuilder(mainTrigger ?: "")
            .append(if (isActivityStateUsed) " " + context.getString(R.string.action_pusher_cause_activity) else "")
            .append(if (isLightStateUsed) " " + context.getString(R.string.action_pusher_cause_light) else "")
            .append(if (isLocationStateUsed) " " + context.getString(R.string.action_pusher_cause_location) else "")
            .append(if (isTimeStateUsed) " " + context.getString(R.string.action_pusher_cause_time) else "")
            .append(if (isWeatherStateUsed) " " + context.getString(R.string.action_pusher_cause_weather) else "")
            .toString()

        val newHistory = item.history.toMutableList().apply {
            add(ActionHistory(cause = causeStr))
        }

        val newItem = item.apply {
            isActivating = true
            lastTriggered = System.currentTimeMillis()
            history = newHistory
        }
        database.updateAction(newItem)
        NotificationUtil.sendAction(newItem)
    }
}