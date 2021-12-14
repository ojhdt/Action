package com.ojhdtapp.action.ui.archive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.Action

class ActionArchiveHistoryViewModel:ViewModel() {
    private val _actionArchiveLive = MutableLiveData<Action>()
    val actionArchiveLive: MutableLiveData<Action> get() = _actionArchiveLive

    fun submit(newActionArchive:Action){
        _actionArchiveLive.value = newActionArchive
    }
}