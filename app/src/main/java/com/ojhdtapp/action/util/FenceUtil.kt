package com.ojhdtapp.action.util

import com.google.android.gms.tasks.OnFailureListener

import android.provider.Settings.System.DATE_FORMAT
import android.util.Log

import com.google.android.gms.awareness.fence.FenceState

import com.google.android.gms.awareness.fence.FenceStateMap

import com.google.android.gms.awareness.fence.FenceQueryResponse

import com.google.android.gms.tasks.OnSuccessListener

import com.google.android.gms.awareness.fence.FenceQueryRequest

import com.google.android.gms.awareness.Awareness
import com.ojhdtapp.action.BaseApplication
import java.util.*


object FenceUtil {
    fun queryFence(fenceKey: String) {
        Awareness.getFenceClient(BaseApplication.context)
            .queryFences(FenceQueryRequest.forFences(listOf(fenceKey)))
            .addOnSuccessListener { response ->
                val map = response.fenceStateMap
                for (fenceKey in map.fenceKeys) {
                    val fenceState = map.getFenceState(fenceKey)
                    Log.d(
                        "aaa", "Fence " + fenceKey + ": "
                                + fenceState!!.currentState
                                + ", was="
                                + fenceState.previousState
                                + ", lastUpdateTime="
                                + String.format(
                            Date(fenceState.lastFenceUpdateTimeMillis).toString()
                        )
                    )
                }
            }
            .addOnFailureListener(OnFailureListener {
                Log.d("aaa", "Could not query fence: $fenceKey")
                return@OnFailureListener
            })
    }
}