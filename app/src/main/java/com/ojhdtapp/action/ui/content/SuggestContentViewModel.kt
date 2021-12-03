package com.ojhdtapp.action.ui.content

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.logic.model.Suggest

class SuggestContentViewModel : ViewModel() {
    // To Save State
    private var _dataLive = MutableLiveData<Suggest>()

    init {
        _dataLive.value = Suggest()
    }

    val dataLive get() = _dataLive

    fun sumbitData(data: Suggest) {
//        Log.d("aaa", "Submit Data")
        _dataLive.value = data
    }

    fun sumbitDataThroughPost(data:Suggest){
        _dataLive.postValue(data)
    }
}