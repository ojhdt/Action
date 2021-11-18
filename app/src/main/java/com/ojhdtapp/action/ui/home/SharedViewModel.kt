package com.ojhdtapp.action.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.R
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.*
import kotlinx.coroutines.*

class SharedViewModel(private val state: SavedStateHandle) : ViewModel() {
    // Action Fragment
    private val _actionNowLive = MutableLiveData<MutableList<Action>>()
    private val _suggestMoreLive = MutableLiveData<MutableList<Suggest>>()
    private val _userInfoLive = MutableLiveData<Any?>()

    val _actionNowTran: LiveData<List<Action>> =
        Transformations.switchMap(_actionNowLive) { Repository.getActionNowLive() }
    val _suggestMoreTran: LiveData<List<Suggest>> =
        Transformations.switchMap(_suggestMoreLive) { Repository.getSuggestMoreLive() }
    val _userInfoTran: LiveData<User> =
        Transformations.switchMap(_userInfoLive) { Repository.getUserInfoLive() }
    val actionNowLive: LiveData<List<Action>> get() = _actionNowTran
    val suggestMoreLive: LiveData<List<Suggest>> get() = _suggestMoreTran
    val userInfoLive: LiveData<User> get() = _userInfoTran

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

    val _gainedAchievementTran: LiveData<List<Achievement>> = Transformations.switchMap(
        _gainedAchievementLive
    ) { Repository.getGainedAchievementLive() }
    val gainedAchievementLive: LiveData<List<Achievement>> get() = _gainedAchievementTran
    val _finishedActionTran: LiveData<List<Action>> = Transformations.switchMap(
        _finishedActionLive
    ) { Repository.getFinishedActionLive() }
    val finishedActionLive: LiveData<List<Action>> get() = _finishedActionTran

    fun gainedAchievementRefresh() {
        _gainedAchievementLive.value = _gainedAchievementLive.value
    }

    fun finishedActionRefresh() {
        _finishedActionLive.value = _finishedActionLive.value
    }

    //Explore Fragment
    private val _weather = MutableLiveData<Boolean>()

    private val _weatherTrans: LiveData<Result<Weather>> = Transformations.switchMap(_weather) {
        if (it) {
            Repository.createTempWeatherLive()
        } else {
            Repository.getWeatherLive()
        }
    }

    val weatherLive: LiveData<Result<Weather>>
        get() = _weatherTrans

    fun weatherRefresh(createTemp: Boolean = false) {
        _weather.value = createTemp
    }

//    private val _settingLive =
//        MutableLiveData<List<Pair<Int, String>>>()
//
//    init {
//        _settingLive.value = listOf(
//            Pair(
//                R.drawable.ic_outline_settings_24,
//                BaseApplication.context.getString(R.string.setting)
//            ),
//            Pair(
//                R.drawable.ic_outline_settings_24,
//                BaseApplication.context.getString(R.string.setting)
//            ),
//            Pair(
//                R.drawable.ic_outline_settings_24,
//                BaseApplication.context.getString(R.string.setting)
//            ),
//            Pair(
//                R.drawable.ic_outline_settings_24,
//                BaseApplication.context.getString(R.string.setting)
//            ),
//            Pair(
//                R.drawable.ic_outline_settings_24,
//                BaseApplication.context.getString(R.string.setting)
//            )
//        )
//    }
//
//    val settingLive: LiveData<List<Pair<Int, String>>> get() = _settingLive
}