package com.ojhdtapp.action.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.R
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
    private val _weather = MutableLiveData<Any?>()

    init {
        val weather = WeatherBlock(
            BaseApplication.context.getString(R.string.loading_location),
            BaseApplication.context.getString(R.string.loading_data),
            WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
            WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
            WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
            WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
            WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
            WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
            WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0)
        )
        val air = WeatherMessageBlock(
            R.drawable.ic_outline_air_24, BaseApplication.context.getString(R.string.air),
            0, 0
        )
        val life = LifeMessageBlock(0,0,0,0)
        _weather.value = Weather(weather, air, life)
    }

    val weatherLive: LiveData<Result<List<Any?>>>
        get() = Transformations.switchMap(_weather) {
            Repository.getWeatherLive()
        }

    fun weatherRefresh() {
        _weather.value = _weather.value
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