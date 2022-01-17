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
        Log.d(
            "aaa",
            this.activityState.toString() + " " + this.lightState.toString() + " " + this.timeState.toString()
        )
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
        mainTrigger: String,
//        shouldNoticeActivity: Boolean? = false,
//        shouldNoticeLight: Boolean? = false,
//        shouldNoticeLocation: Boolean? = false
    ) {
        if (System.currentTimeMillis() - triggerTime > 60000) {
            triggerTime = System.currentTimeMillis()
            val job = Job()
            CoroutineScope(job).launch {
                var tempList: List<Action> = listOf()
                when (mainTrigger) {
                    "activity" -> {
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            lightState,
                            locationState,
                            timeState,
                            weatherState
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, true, true, true, true
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            lightState,
                            locationState,
                            timeState,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, true, true, true, false
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            lightState,
                            locationState,
                            -1,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, true, true, false, false
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            lightState,
                            -1,
                            -1,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, true, false, false, false
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            -1,
                            -1,
                            -1,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, false, false, false, false
                            )
                            return@launch
                        }
                    }
                    "light" -> {
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            lightState,
                            locationState,
                            timeState,
                            weatherState
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, true, true, true, true
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            lightState,
                            locationState,
                            timeState,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, true, true, true, false
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            lightState,
                            locationState,
                            -1,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, true, true, false, false
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            lightState,
                            -1,
                            -1,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, true, false, false, false
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            -1,
                            lightState,
                            -1,
                            -1,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                false, true, false, false, false
                            )
                            return@launch
                        }
                    }
                    "location" -> {
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            lightState,
                            locationState,
                            timeState,
                            weatherState
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, true, true, true, true
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            lightState,
                            locationState,
                            timeState,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, true, true, true, false
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            lightState,
                            locationState,
                            -1,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, true, true, false, false
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            -1,
                            locationState,
                            -1,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, false, true, false, false
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            -1,
                            -1,
                            locationState,
                            -1,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                false, false, true, false, false
                            )
                            return@launch
                        }
                    }
                    "time" -> {
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            lightState,
                            locationState,
                            timeState,
                            weatherState
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, true, true, true, true
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            lightState,
                            locationState,
                            timeState,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, true, true, true, false
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            lightState,
                            -1,
                            timeState,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, true, false, true, false
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            activityState,
                            -1,
                            -1,
                            timeState,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                true, false, false, true, false
                            )
                            return@launch
                        }
                        tempList = Repository.loadAvailableActionByConditions(
                            -1,
                            -1,
                            -1,
                            timeState,
                            -1
                        )
                        if (tempList.isNotEmpty()) {
                            editAndPushAction(
                                selectActionRandomlyByWeight(tempList),
                                false, false, false, true, false
                            )
                            return@launch
                        }
                    }
                    "weather" -> {}
                    else -> {
                        tempList = Repository.loadAvailableActionByConditions(-1,-1,-1,-1,-1)
                        editAndPushAction(
                            selectActionRandomlyByWeight(tempList),
                            false, false, false, false, false
                        )
                    }
                }
            }
        }
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
            if (ssum >= sum) {
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
        isWeatherStateUsed: Boolean = false
    ) {
        val causeStr = StringBuilder()
            .append(if(isActivityStateUsed) context.getString(R.string.action_pusher_cause_activity) else "")
            .append(" ")
            .append(if(isLightStateUsed) context.getString(R.string.action_pusher_cause_light) else "")
            .append(" ")
            .append(if(isLocationStateUsed) context.getString(R.string.action_pusher_cause_location) else "")
            .append(" ")
            .append(if(isTimeStateUsed) context.getString(R.string.action_pusher_cause_time) else "")
            .append(" ")
            .append(if(isWeatherStateUsed) context.getString(R.string.action_pusher_cause_weather) else "")
            .toString()

        val newHistory = item.history.toMutableList().apply {
            add(ActionHistory(cause = causeStr))
        }

        val newItem = item.apply {
            isActivating = true
            lastTriggered = System.currentTimeMillis()
            history = newHistory
        }
        database.insertAction(newItem)
        NotificationUtil.sendAction(newItem)
    }
}