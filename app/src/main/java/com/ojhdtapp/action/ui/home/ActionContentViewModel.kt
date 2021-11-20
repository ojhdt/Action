package com.ojhdtapp.action.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ojhdtapp.action.logic.model.Action

class ActionContentViewModel : ViewModel() {
//    private var _finished = MutableLiveData<Boolean>()
//    init {
//        _finished.value = false
//    }
//    val finished get() = _finished
//
//    fun setFinishedValue(value:Boolean){
//        _finished.value = value
//    }

    // To Save State
    private var _dataLive = MutableLiveData<Action>()

    init {
        _dataLive.value = Action()
    }

    val dataLive get() = _dataLive

    fun sumbitData(data: Action) {
        _dataLive.value = data
    }

    fun switchFinishedStatus() {
        sumbitData(_dataLive.value!!.apply {
            finished = !finished
        })
    }
}