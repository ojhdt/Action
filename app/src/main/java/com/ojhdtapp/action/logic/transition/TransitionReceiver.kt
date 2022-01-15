package com.ojhdtapp.action.logic.transition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity

class TransitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (ActivityTransitionResult.hasResult(intent)) {
            val result = intent?.let { ActivityTransitionResult.extractResult(it) }!!
            for (event in result.transitionEvents) {
                // chronological sequence of events....
                when(event.activityType){
                    DetectedActivity.IN_VEHICLE -> {
                        Log.d("aaa", "IN_VEHICLE")
                    }
                    DetectedActivity.ON_BICYCLE -> {
                        Log.d("aaa", "ON_BICYCLE")
                    }
                    DetectedActivity.ON_FOOT -> {
                        Log.d("aaa", "ON_FOOT")
                    }
                    DetectedActivity.TILTING -> {
                        Log.d("aaa", "TILTING")
                    }
                }
            }
        }
    }
}