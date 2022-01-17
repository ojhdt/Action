package com.ojhdtapp.action.logic.detector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

class TimeChangeReceiver : BroadcastReceiver() {
    private var systemCalendarHour:Int = 0
    private var lastTimeState:Int = -1
    private var timeState:Int = -1
    private val pusher = ActionPusher.getPusher()

    override fun onReceive(context: Context?, intent: Intent?) {
//        Log.d("aaa", System.currentTimeMillis().toString())
        systemCalendarHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if(lastTimeState == -1) lastTimeState = getTimeState(systemCalendarHour)
        else{
            timeState = getTimeState(systemCalendarHour)
            if(timeState != lastTimeState){
                pusher.submitState(timeState = timeState)
            }
        }
    }

    private fun getTimeState(hour: Int) = when (hour) {
        in 0..4 -> ActionPusher.TIME_MIDNIGHT
        in 5..6 -> ActionPusher.TIME_EARLY_MORNING
        in 7..10 -> ActionPusher.TIME_MORNING
        in 11..13 -> ActionPusher.TIME_NOON
        in 14..16 -> ActionPusher.TIME_AFTERNOON
        in 17..18 -> ActionPusher.TIME_DUSK
        in 19..23 -> ActionPusher.TIME_EVENING
        else -> -1
    }
}