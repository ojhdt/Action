package com.ojhdtapp.action.logic.detector

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import com.ojhdtapp.action.logic.NotificationActionReceiver
import com.ojhdtapp.action.util.NotificationUtil
import kotlin.concurrent.thread
import kotlin.math.abs

class DetectService : Service() {

    class DetectBinder : Binder()

    lateinit var notificationActionReceiver: NotificationActionReceiver

    val context = BaseApplication.context
    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }
    private val pusher = ActionPusher.getPusher()
    lateinit var fenceReceiver: FenceReceiver
    lateinit var fencePendingIntent: PendingIntent
    lateinit var transitionPendingIntent: PendingIntent
    lateinit var transitionReceiver: TransitionReceiver
    private lateinit var fenceMap: Map<String, AwarenessFence>
    private lateinit var timeChangeReceiver: TimeChangeReceiver
    private val FENCE_ACTION = "fence_receiver_action"
    private val TRANSITION_ACTION = "transition_receiver_action"

    private lateinit var sensorManager: SensorManager

    // Accelerometer Sensor
    private var shakeTime: Long = 0
    private var showTime: Long = 0
    private var triggerTime: Long = 0
    // Light Sensor


    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()

        // FenceMap
        fenceMap =
            mapOf<String, AwarenessFence>(
                "headphonePlug" to HeadphoneFence.during(HeadphoneState.PLUGGED_IN),
                "inVehicle" to DetectedActivityFence.during(DetectedActivityFence.IN_VEHICLE),
                "onBicycle" to DetectedActivityFence.during(DetectedActivityFence.ON_BICYCLE),
                "onFoot" to DetectedActivityFence.during(DetectedActivityFence.ON_FOOT),
            )

        // Register Notification Action Receiver
        notificationActionReceiver = NotificationActionReceiver()
        registerReceiver(notificationActionReceiver, IntentFilter().apply {
            addAction(NotificationUtil.ACTION_FINISHED)
            addAction(NotificationUtil.ACTION_IGNORED)
        })

        // Register as Foreground Service
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val channel = NotificationChannel(
            "foreground",
            context.getString(R.string.channel_name_foreground_service),
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            description = context.getString(R.string.channel_name_foreground_service_des)
        }
        manager.createNotificationChannel(channel)
        val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
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

        // Register TimeChangeReceiver
        timeChangeReceiver = TimeChangeReceiver()
        registerReceiver(timeChangeReceiver, IntentFilter().apply {
            addAction("android.intent.action.TIME_TICK")
        })

        // Register Accelerometer Sensor
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            Log.d("aaa", "Accelerometer was successfully registered.")
            sharedPreference.edit()
                .putBoolean("isAccelerometerSensorRegistered", true)
                .apply()
            initAccelerometerSensorEvent()
        } else {
            Log.d("aaa", "Accelerometer could not be registered");
            sharedPreference.edit()
                .putBoolean("isAccelerometerSensorRegistered", false)
                .apply()
        }

        // Register Light Sensor
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            Log.d("aaa", "Light was successfully registered.")
            sharedPreference.edit()
                .putBoolean("isLightSensorRegistered", true)
                .apply()
            initLightSensorEvent()
        } else {
            Log.d("aaa", "Light could not be registered");
            sharedPreference.edit()
                .putBoolean("isLightSensorRegistered", false)
                .apply()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return DetectBinder()
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("aaa", "DetectService Start")
        thread {
            // Fence
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
        stopForeground(true)
        // Notification
        unregisterReceiver(notificationActionReceiver)
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
        // TimeChanger
        unregisterReceiver(transitionReceiver)
        unregisterReceiver(timeChangeReceiver)
        // Accelerometer
        sensorManager.unregisterListener(accelerometerSensorEventListener)
        // Light
        sensorManager.unregisterListener(lightSensorEventListener)

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

    // Accelerometer Functions
    private val accelerometerSensorEventListener: SensorEventListener =
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val values = event.values
                //X轴方向的重力加速度，向右为正
                val x = values[0]
                //Y轴方向的重力加速度，向前为正
                val y = values[1]
                //Z轴方向的重力加速度，向上为正
                val z = values[2]
                val medumValue = 14
                //判断是否抬手
                if (abs(x) > medumValue || abs(y) > medumValue || abs(z) > medumValue) {
                    shakeTime = System.currentTimeMillis()
                }
                if (z < 9 && z > 2 && -2 < x && x < 2 && 4 < y && y < 10) {
                    showTime = System.currentTimeMillis()
                    if (showTime - shakeTime in 1..800 && showTime - triggerTime > 10000) {
                        if (showTime - triggerTime > 1200000) {
                            // long stay
                            pusher.tryPushingNewAction("设备久置后移动")
                        } else {
                            // normal
                            pusher.tryPushingNewAction("设备移动")
                        }
                        shakeTime = 0
                        triggerTime = System.currentTimeMillis()
                        Log.d("sensor", "Accelerometer Worked!!")
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

    @SuppressLint("InvalidWakeLockTag")
    private fun initAccelerometerSensorEvent() {
        val accelerometer: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(
            accelerometerSensorEventListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    // Light Functions
    private val lightSensorEventListener: SensorEventListener = object : SensorEventListener {
        var light: Float = 0f
        var lastState: Int = -1
        var nowState: Int = -1

        //        var times: Int = 0
        var triggerTime = System.currentTimeMillis()
        override fun onSensorChanged(event: SensorEvent?) {
            light = event?.values?.get(0) ?: 0f
            nowState = getLightState(light)
//            Log.d("sensor", nowState.toString())
            if (lastState == -1) lastState = nowState
            else {
                if (nowState != lastState && System.currentTimeMillis() - triggerTime > 0) {
                    lastState = nowState
                    triggerTime = System.currentTimeMillis()
                    Log.d("sensor", "Light triggered")
                    pusher.submitState(lightState = nowState)
                    pusher.tryPushingNewAction("light")
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    private fun initLightSensorEvent() {
        val lightSensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        sensorManager.registerListener(
            lightSensorEventListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    private fun getLightState(light: Float) = when (light) {
        in 0f..20f -> ActionPusher.LIGHT_FULL_MOON
        in 20f..SensorManager.LIGHT_SUNRISE -> ActionPusher.LIGHT_SUNRISE
        in SensorManager.LIGHT_SUNRISE..SensorManager.LIGHT_SHADE -> ActionPusher.LIGHT_SHADE
        in SensorManager.LIGHT_SHADE..SensorManager.LIGHT_SUNLIGHT_MAX -> ActionPusher.LIGHT_SUNLIGHT
        else -> -1
    }
}