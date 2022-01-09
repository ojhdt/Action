package com.ojhdtapp.action.logic.awareness

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.AwarenessFence
import com.google.android.gms.awareness.fence.DetectedActivityFence
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.awareness.fence.HeadphoneFence
import com.google.android.gms.awareness.state.HeadphoneState
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.util.FenceUtil

class FenceService : Service() {
    lateinit var fenceReceiver: FenceReceiver
    lateinit var pendingIntent: PendingIntent
    val context = BaseApplication.context

    override fun onCreate() {
        super.onCreate()
        Log.d("aaa", "Fence Service created")

        // Register BroadCastReceiver
        val intent = Intent("fence_receiver_action")
        fenceReceiver = FenceReceiver()
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        registerReceiver(fenceReceiver, IntentFilter("fence_receiver_action"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("aaa", "Fence Service started")
        val headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN)
//        val walkingFence = DetectedActivityFence.during(DetectedActivityFence.WALKING)
//        val fenceMap =
//            mapOf<String,AwarenessFence>(
//                "headphonePlug" to HeadphoneFence.during(HeadphoneState.PLUGGED_IN),
//                "walking" to DetectedActivityFence.during(DetectedActivityFence.WALKING)
//            )
//        val fenceUpdateRequest = FenceUpdateRequest.Builder().apply {
//            fenceMap.forEach { t, u ->
//                addFence(t,u,pendingIntent)
//            }
//        }.build()
        val fenceUpdateRequest =FenceUpdateRequest.Builder().apply {
            addFence("headphonePlug", headphoneFence, pendingIntent)
//            addFence("walking", walkingFence, pendingIntent)
        }.build()
        Awareness.getFenceClient(context).updateFences(fenceUpdateRequest).addOnSuccessListener {
            Log.d("aaa", "Fence was successfully registered.")
        }.addOnFailureListener {
            Log.e("aaa", "Fence could not be registered: $it");
        }
        FenceUtil.queryFence("headphonePlug")
        FenceUtil.queryFence("walking")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        return FenceBinder()
    }

    override fun onDestroy() {
        unregisterReceiver(fenceReceiver)
        super.onDestroy()
        Log.d("aaa", "Fence Service destroyed")
    }

    class FenceBinder : Binder() {

    }
}