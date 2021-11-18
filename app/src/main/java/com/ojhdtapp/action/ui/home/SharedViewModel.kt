package com.ojhdtapp.action.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.R
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.*

class SharedViewModel(private val state: SavedStateHandle) : ViewModel() {
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
    private val _weather = MutableLiveData<Boolean>()

    val _weatherTrans: LiveData<Result<List<Any?>>> = Transformations.switchMap(_weather) {
        Log.d("aaa", it.toString() + state.contains("WEATHERTEMP").toString())
        Log.d("aaa", state.keys().toString())
        if (it == true && state.contains("WEATHERTEMP")) {
            Log.d("aaa", "From Temp")
            state.getLiveData("WEATHERTEMP")
        } else {
            Log.d("aaa", "From Net")
            val result = Repository.getWeatherLive()
            state["WEATHERTEMP"] = result.value
            Log.d("aaa", state.keys().toString())
            result
        }
    }

    val weatherLive: LiveData<Result<List<Any?>>>
        get() = _weatherTrans

    fun weatherRefresh(fromTemp: Boolean = false) {
        _weather.value = fromTemp
    }

    private val _settingLive =
        MutableLiveData<List<Pair<Int, String>>>()

    init {
        _settingLive.value = listOf(
            Pair(
                R.drawable.ic_outline_settings_24,
                BaseApplication.context.getString(R.string.setting)
            ),
            Pair(
                R.drawable.ic_outline_settings_24,
                BaseApplication.context.getString(R.string.setting)
            ),
            Pair(
                R.drawable.ic_outline_settings_24,
                BaseApplication.context.getString(R.string.setting)
            )
        )
    }

    val settingLive: LiveData<List<Pair<Int, String>>> get() = _settingLive
}