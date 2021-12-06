package com.ojhdtapp.action.ui.archive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.Suggest

class SuggestHistoryTabViewModel : ViewModel() {
    private val _suggestReadLive = MutableLiveData<MutableList<Suggest>>()
    private val _suggestReadTran: LiveData<List<Suggest>> =
        Transformations.switchMap(_suggestReadLive) { Repository.getReadSuggestLive() }
    val suggestReadLive: LiveData<List<Suggest>> get() = _suggestReadTran

    fun readSuggestRefresh(){
        _suggestReadLive.value = _suggestReadLive.value
    }
}