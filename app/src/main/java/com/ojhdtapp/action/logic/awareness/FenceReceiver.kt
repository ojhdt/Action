package com.ojhdtapp.action.logic.awareness

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.awareness.fence.FenceState
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.util.NotificationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class FenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val state = intent?.let { FenceState.extract(it) }
        state?.let {
            when(it.fenceKey){
                "headphonePlug" -> {
                    when(it.currentState){
                        FenceState.TRUE -> {
                            CoroutineScope(Dispatchers.Default).launch {
                                Repository.getAllActionLive().value?.get(0)?.let {
                                    NotificationUtil.sendAction(it)
                                }
                            }
                        }
                        else -> {}
                    }
                }
                else -> {}
            }
        }
    }
}