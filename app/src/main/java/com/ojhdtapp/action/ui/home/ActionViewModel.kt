package com.ojhdtapp.action.ui.home

import androidx.lifecycle.*
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.Action

class ActionViewModel : ViewModel() {
    private var _actionNowLive = MutableLiveData<MutableList<Action>>()

    val actionNowLive: LiveData<List<Action>> get() = Transformations.switchMap(_actionNowLive) { Repository.getActionNowLive() }

    fun refresh() {
        _actionNowLive.value = _actionNowLive.value
    }
}