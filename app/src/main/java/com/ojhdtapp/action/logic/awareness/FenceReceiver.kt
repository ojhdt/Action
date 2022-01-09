package com.ojhdtapp.action.logic.awareness

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.awareness.fence.FenceState

class FenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val state = intent?.let { FenceState.extract(it) }
        state?.let {
            when(it.fenceKey){
                "headphonePlug" -> {
                    when(it.currentState){
                        FenceState.TRUE -> {
                            Log.d("aaa", "success")
                        }
                        else -> {}
                    }
                }
                else -> {}
            }
        }
    }
}