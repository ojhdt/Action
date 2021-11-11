package com.ojhdtapp.action.ui.home

import androidx.lifecycle.*
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.logic.model.Suggest

class ActionViewModel : ViewModel() {
    private var _actionNowLive = MutableLiveData<MutableList<Action>>()
    private var _suggestMoreLive = MutableLiveData<MutableList<Suggest>>()

    val actionNowLive: LiveData<List<Action>> get() = Transformations.switchMap(_actionNowLive) { Repository.getActionNowLive() }
    val suggestMoreLive: LiveData<List<Suggest>> get() = Transformations.switchMap(_suggestMoreLive) { Repository.getSuggestMoreLive() }

    fun refresh() {
        _actionNowLive.value = _actionNowLive.value
        _suggestMoreLive.value = _suggestMoreLive.value
    }
}