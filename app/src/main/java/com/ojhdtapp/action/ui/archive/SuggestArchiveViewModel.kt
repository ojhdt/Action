package com.ojhdtapp.action.ui.archive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.Suggest

class SuggestArchiveViewModel : ViewModel() {
    private val _suggestReadLive = MutableLiveData<MutableList<Suggest>>()
    private val _suggestReadTran: LiveData<List<Suggest>> =
        Transformations.switchMap(_suggestReadLive) { Repository.getReadSuggestLive() }
    val suggestReadLive: LiveData<List<Suggest>> get() = _suggestReadTran

    fun readSuggestRefresh(){
        _suggestReadLive.value = _suggestReadLive.value
    }

    private val _suggestArchivedLive = MutableLiveData<MutableList<Suggest>>()
    private val _suggestArchivedTran: LiveData<List<Suggest>> =
        Transformations.switchMap(_suggestArchivedLive) { Repository.getArchivedSuggestLive() }
    val suggestArchivedLive: LiveData<List<Suggest>> get() = _suggestArchivedTran

    fun archivedSuggestRefresh(){
        _suggestArchivedLive.value = _suggestArchivedLive.value
    }
}