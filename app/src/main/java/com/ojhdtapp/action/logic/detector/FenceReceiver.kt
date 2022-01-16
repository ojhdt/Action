package com.ojhdtapp.action.logic.detector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ojhdtapp.action.util.FenceUtil

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