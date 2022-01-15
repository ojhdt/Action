package com.ojhdtapp.action.logic.awareness

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.google.android.gms.awareness.fence.FenceState
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.util.FenceUtil
import com.ojhdtapp.action.util.NotificationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class FenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
//        Log.d("aaa", "Receiver received")
        FenceUtil.queryFence(listOf("headphonePlug", "walking"))
        val fenceKeyList = listOf<String>("headphonePlug", "walking")
        val result = FenceUtil.queryFenceForResult(fenceKeyList)
        result.forEach {
            Log.d("aaa", it.fenceKey)
            when(it.fenceKey){
                "headphonePlug" -> {
                    if(it.state == 2){

                    }
                }
                "walking" -> {}
            }
        }

//        val state = FenceState.extract(intent) as FenceState
//        if (TextUtils.equals(state.fenceKey, "headphonePlug")) {
//            when (state.currentState) {
//                FenceState.TRUE -> {
//                    Log.d("aaa", "TRUE")
//                }
//                FenceState.FALSE -> {
//                    Log.d("aaa", "FALSE")
//                }
//                FenceState.UNKNOWN -> {
//                    Log.d("aaa", "UNKNOWN")
//                }
//            }
//        }

//        if (TextUtils.equals(state.fenceKey, "walking")) {
//            when (state.currentState) {
//                FenceState.TRUE -> {
//                    Log.d("aaa", "TRUE")
//                }
//                FenceState.FALSE -> {
//                    Log.d("aaa", "FALSE")
//                }
//                FenceState.UNKNOWN -> {
//                    Log.d("aaa", "UNKNOWN")
//                }
//            }
//        }
    }
}