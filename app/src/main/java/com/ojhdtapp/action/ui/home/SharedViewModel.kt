package com.ojhdtapp.action.ui.home

import android.util.Log
import androidx.coordinatorlayout.widget.DirectedAcyclicGraph
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.Achievement
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.logic.model.Suggest
import com.ojhdtapp.action.logic.model.User

class SharedViewModel : ViewModel() {
    // Action Fragment
    private val _actionNowLive = MutableLiveData<MutableList<Action>>()
    private val _suggestMoreLive = MutableLiveData<MutableList<Suggest>>()
    private val _userInfoLive = MutableLiveData<Any?>()

    val actionNowLive: LiveData<List<Action>> get() = Transformations.switchMap(_actionNowLive) { Repository.getActionNowLive() }
    val suggestMoreLive: LiveData<List<Suggest>> get() = Transformations.switchMap(_suggestMoreLive) { Repository.getSuggestMoreLive() }
    val userInfoLive: LiveData<User> get() = Transformations.switchMap(_userInfoLive) { Repository.getUserInfoLive() }

    fun actionRefresh() {
        _actionNowLive.value = _actionNowLive.value
    }

    fun suggestRefresh() {
        _suggestMoreLive.value = _suggestMoreLive.value
    }

    fun getUserInfo() {
        _userInfoLive.value = _userInfoLive.value
    }

    // Achievement Fragment
    private val _gainedAchievementLive = MutableLiveData<MutableList<Achievement>>()
    private val _finishedActionLive = MutableLiveData<MutableList<Action>>()

    val gainedAchievementLive: LiveData<List<Achievement>>
        get() = Transformations.switchMap(
            _gainedAchievementLive
        ) { Repository.getGainedAchievementLive() }
    val finishedActionLive: LiveData<List<Action>>
        get() = Transformations.switchMap(
            _finishedActionLive
        ) { Repository.getFinishedActionLive() }

    fun gainedAchievementRefresh() {
        Log.d("aaa", "GetDataA")
        _gainedAchievementLive.value = _gainedAchievementLive.value
    }

    fun finishedActionRefresh() {
        Log.d("aaa", "GetDataB")
        _finishedActionLive.value = _finishedActionLive.value
    }
}