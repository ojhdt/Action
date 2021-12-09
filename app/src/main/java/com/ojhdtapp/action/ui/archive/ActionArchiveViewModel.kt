package com.ojhdtapp.action.ui.archive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.Action

class ActionArchiveViewModel : ViewModel() {
    private val _actionLive = MutableLiveData<MutableList<Action>>()
    private val _actionTran: LiveData<List<Action>> =
        Transformations.switchMap(_actionLive) { Repository.getAllActionLive() }
    val actionLive: LiveData<List<Action>> get() = _actionTran

    fun actionRefresh(){
        _actionLive.value = _actionLive.value
    }
}