package com.ojhdtapp.action.logic.detector

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.AwarenessFence
import com.google.android.gms.awareness.fence.DetectedActivityFence
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.awareness.fence.HeadphoneFence
import com.google.android.gms.awareness.state.HeadphoneState
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.MainActivity
import com.ojhdtapp.action.R
import kotlin.concurrent.thread

class DetectService : Service() {
    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }
    lateinit var fenceReceiver: FenceReceiver
    lateinit var fencePendingIntent: PendingIntent
    lateinit var transitionPendingIntent: PendingIntent
    lateinit var transitionReceiver: TransitionReceiver
    private lateinit var fenceMap: Map<String, AwarenessFence>
    val context = BaseApplication.context
    private val FENCE_ACTION = "fence_receiver_action"
    private val TRANSITION_ACTION = "transition_receiver_action"

    override fun onCreate() {
        super.onCreate()

        // Register as Foreground Service
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            "foreground",
            context.getString(R.string.channel_name_foreground_service),
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            description = context.getString(R.string.channel_name_foreground_service_des)
        }
        manager.createNotificationChannel(channel)
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        val mainActivityPendingIntent =
            PendingIntent.getActivity(this, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, "foreground")
            .setContentTitle(context.getString(R.string.foreground_service_notification_title))
            .setContentText(context.getString(R.string.foreground_service_notification_text))
            .setSmallIcon(R.drawable.ic_outline_android_24)
            .setContentIntent(mainActivityPendingIntent)
            .setAutoCancel(true)
            .build()
        if (sharedPreference.getBoolean("foreground_service", false)) {
            Log.d("aaa", "foreground running")
            startForeground(-1, notification)
        }


        // Register Fence BroadCastReceiver
        val fenceIntent = Intent(FENCE_ACTION)
        fenceReceiver = FenceReceiver()
        fencePendingIntent =
            PendingIntent.getBroadcast(context, 0, fenceIntent, PendingIntent.FLAG_IMMUTABLE)
        registerReceiver(fenceReceiver, IntentFilter(FENCE_ACTION))

        val transitionIntent = Intent(TRANSITION_ACTION)
        transitionReceiver = TransitionReceiver()
        transitionPendingIntent =
            PendingIntent.getBroadcast(context, 0, transitionIntent, PendingIntent.FLAG_IMMUTABLE)
        registerReceiver(transitionReceiver, IntentFilter(TRANSITION_ACTION))
    }

    override fun onBind(intent: Intent): IBinder {
        return DetectBinder()
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("aaa", "DetectService Start")
        thread {
            // Fence
            fenceMap =
                mapOf<String, AwarenessFence>(
                    "headphonePlug" to HeadphoneFence.during(HeadphoneState.PLUGGED_IN),
                    "walking" to DetectedActivityFence.during(DetectedActivityFence.WALKING)
                )
            val fenceUpdateRequest = FenceUpdateRequest.Builder().apply {
                fenceMap.forEach { (t, u) ->
                    addFence(t, u, fencePendingIntent)
                }
            }.build()
            Awareness.getFenceClient(context).updateFences(fenceUpdateRequest)
                .addOnSuccessListener {
                    Log.d("aaa", "Fence was successfully registered.")
                    sharedPreference.edit()
                        .putBoolean("isAwarenessRegistered", true)
                        .apply()
                }.addOnFailureListener {
                Log.e("aaa", "Fence could not be registered: $it");
                    sharedPreference.edit()
                        .putBoolean("isAwarenessRegistered", false)
                        .apply()
            }

            // Transition
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
                .requestActivityTransitionUpdates(request, transitionPendingIntent)
            task.addOnSuccessListener {
                Log.d("aaa", "Transition was successfully registered")
                sharedPreference.edit()
                    .putBoolean("isTransitionRegistered", true)
                    .apply()
            }
            task.addOnFailureListener {
                Log.d("aaa", "Transition could not be registered")
                sharedPreference.edit()
                    .putBoolean("isTransitionRegistered", false)
                    .apply()
                Log.d("aaa", it.message.toString())
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        Log.d("aaa", "DetectService Destroyed")
        // Fence
        val fenceUpdateRequest = FenceUpdateRequest.Builder().apply {
            fenceMap.forEach { (t, u) ->
                removeFence(t)
            }
        }.build()
        Awareness.getFenceClient(context).updateFences(fenceUpdateRequest).addOnSuccessListener {
            Log.d("aaa", "Fence was successfully unregistered.")
        }.addOnFailureListener {
            Log.e("aaa", "Fence could not be unregistered: $it");
        }
        unregisterReceiver(fenceReceiver)

        // Transition
        val task = ActivityRecognition.getClient(context)
            .removeActivityTransitionUpdates(transitionPendingIntent)
        task.addOnSuccessListener {
            Log.d("aaa", "Transition was successfully unregistered")
        }
        task.addOnFailureListener {
            it.message?.let { it1 -> Log.d("aaa", it1) }
        }
        unregisterReceiver(transitionReceiver)

        super.onDestroy()
//        if (sharedPreference.getBoolean("restart_service", false)) {
//            sharedPreference.edit()
//                .putBoolean("restart_service", false)
//                .apply()
//            context.startService(Intent(context, DetectService::class.java))
//        }

        sharedPreference.edit()
            .putBoolean("isAwarenessRegistered", false)
            .putBoolean("isTransitionRegistered", false)
            .apply()
    }

    class DetectBinder : Binder()
}