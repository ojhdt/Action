package com.ojhdtapp.action.logic.awareness

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.AwarenessFence
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.awareness.fence.HeadphoneFence
import com.ojhdtapp.action.BaseApplication

class FenceService : Service() {
    lateinit var fenceReceiver: FenceReceiver
    lateinit var pendingIntent: PendingIntent
    val context = BaseApplication.context

    override fun onCreate() {
        super.onCreate()
        Log.d("aaa", "Fence Service created")

        // Register BroadCastReceiver
        val intent = Intent("fense_receiver_action")
        fenceReceiver = FenceReceiver()
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        registerReceiver(fenceReceiver, IntentFilter("fense_receiver_action"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("aaa", "Fence Service started")
        val fenceMap =
            mapOf<String,AwarenessFence>(
                "headphonePlug" to HeadphoneFence.pluggingIn(),
            )
        val fenceUpdateRequest = FenceUpdateRequest.Builder().apply {
            fenceMap.forEach { t, u ->
                addFence(t,u,pendingIntent)
            }
        }.build()
        Awareness.getFenceClient(context).updateFences(fenceUpdateRequest)
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