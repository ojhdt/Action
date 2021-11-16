package com.ojhdtapp.action.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.*

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
        _gainedAchievementLive.value = _gainedAchievementLive.value
    }

    fun finishedActionRefresh() {
        _finishedActionLive.value = _finishedActionLive.value
    }

    //Explore Fragment
    private val _weatherMessage = MutableLiveData<WeatherBlock>()
    private val _airMessage = MutableLiveData<WeatherMessageBlock>()
    private val _lifeMessage = MutableLiveData<LifeMessageBlock>()

    val weatherMessageLive: LiveData<WeatherBlock>
        get() = Transformations.switchMap(_weatherMessage) {
            Repository.getWeatherMessageLive()
        }
    val airMessageLive: LiveData<WeatherMessageBlock>
        get() = Transformations.switchMap(_airMessage) {
            Repository.getAirMessageLive()
        }
    val lifeMessageLive: LiveData<LifeMessageBlock>
        get() = Transformations.switchMap(_lifeMessage) {
            Repository.getLifeMessageLive()
        }
}