package com.ojhdtapp.action.logic.transition

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.logic.detector.TransitionReceiver

class _TransitionService : Service() {
    lateinit var pendingIntent: PendingIntent
    lateinit var transitionReceiver: TransitionReceiver
    val context = BaseApplication.context

    override fun onCreate() {
        super.onCreate()

        // Register BroadCastReceiver
        val intent = Intent("transition_receiver_action")
        transitionReceiver = TransitionReceiver()
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        registerReceiver(transitionReceiver, IntentFilter("transition_receiver_action"))
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val transitions = mutableListOf<ActivityTransition>()
        transitions += ActivityTransition.Builder()
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
            .setActivityType(DetectedActivity.IN_VEHICLE)
            .build()
        transitions += ActivityTransition.Builder()
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
            .setActivityType(DetectedActivity.ON_BICYCLE)
            .build()
        transitions += ActivityTransition.Builder()
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
            .setActivityType(DetectedActivity.ON_FOOT)
            .build()
//        transitions += ActivityTransition.Builder()
//            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
//            .setActivityType(DetectedActivity.TILTING)
//            .build()

        val request = ActivityTransitionRequest(transitions)
        val task = ActivityRecognition.getClient(context)
            .requestActivityTransitionUpdates(request,pendingIntent)
        task.addOnSuccessListener {
            Log.d("aaa", "Transition was successfully registered")
        }
        task.addOnFailureListener {
            Log.d("aaa", "Transition could not be registered")
            Log.d("aaa", it.message.toString())
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        return TransitionBinder()
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        val task = ActivityRecognition.getClient(context)
            .removeActivityTransitionUpdates(pendingIntent)
        task.addOnSuccessListener {
            Log.d("aaa", "Transition was successfully unregistered")
        }
        task.addOnFailureListener {
            it.message?.let { it1 -> Log.d("aaa", it1) }
        }
        unregisterReceiver(transitionReceiver)
        super.onDestroy()
    }

    class TransitionBinder: Binder()
}