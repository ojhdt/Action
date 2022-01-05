package com.ojhdtapp.action.ui.home

import android.app.Application
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.Event
import com.ojhdtapp.action.R
import com.ojhdtapp.action.getUriToDrawable
import com.ojhdtapp.action.logic.AppDataBase
import com.ojhdtapp.action.logic.LeanCloudDataBase
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.*
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SharedViewModel(application: Application, private val state: SavedStateHandle) :
    AndroidViewModel(
        application
    ) {

    private val dataBase = AppDataBase.getDataBase()
    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(application)
    }

    // SnackBar Messages
    private val _snackBarMessageLive = MutableLiveData<Event<String>>()
    val snackBarMessageLive get() = _snackBarMessageLive

    // Transition Type
    private val _shouldSetTransitionLive = MutableLiveData<Boolean>()
    val shouldSetTransitionLive get() = _shouldSetTransitionLive

    fun setShouldSetTransitionLive(value: Boolean) {
        _shouldSetTransitionLive.value = value
    }

    // Action Fragment
    private val _actionNowLive = MutableLiveData<MutableList<Action>>()
    private val _suggestMoreLive = MutableLiveData<MutableList<Suggest>>()

    private val _actionNowTran: LiveData<List<Action>> =
        Transformations.switchMap(_actionNowLive) { Repository.getActionNowLive() }
    private val _suggestMoreTran: LiveData<List<Suggest>> =
        Transformations.switchMap(_suggestMoreLive) { Repository.getSuggestMoreLive() }
    val actionNowLive: LiveData<List<Action>> get() = _actionNowTran
    val suggestMoreLive: LiveData<List<Suggest>> get() = _suggestMoreTran

    fun actionRefresh() {
        _actionNowLive.value = _actionNowLive.value
    }

    fun suggestRefresh() {
        _suggestMoreLive.value = _suggestMoreLive.value
    }

    suspend fun storeSuggestFromCloud(type: Int) {
        try {
            // suspend
            val result = LeanCloudDataBase.getNewSuggest(type)
            dataBase.suggestDao().insertSuggest(result)
            _snackBarMessageLive.postValue(Event(getApplication<Application>().getString(R.string.network_success)))
        } catch (e: Exception) {
//            Log.d("aaa", e.toString())
            _snackBarMessageLive.postValue(Event(getApplication<Application>().getString(R.string.network_error)))
        }
    }


    // Achievement Fragment
    private val _gainedAchievementLive = MutableLiveData<MutableList<Achievement>>()
    private val _allActionLive = MutableLiveData<MutableList<Action>>()
    private val _isSortByTimeLive = MutableLiveData<Boolean>()

    init {
        _isSortByTimeLive.value = true
    }

    private val _gainedAchievementTran: LiveData<List<Achievement>> = Transformations.switchMap(
        _gainedAchievementLive
    ) { Repository.getGainedAchievementLive() }
    val gainedAchievementLive: LiveData<List<Achievement>> get() = _gainedAchievementTran
    private val _allActionTran: LiveData<List<Action>> = Transformations.switchMap(
        _allActionLive
    ) { Repository.getAllActionLive() }
    val allActionLive: LiveData<List<Action>> get() = _allActionTran
    val isSortByTimeLive: LiveData<Boolean> get() = _isSortByTimeLive

    fun gainedAchievementRefresh() {
        _gainedAchievementLive.value = _gainedAchievementLive.value
    }

    fun finishedActionRefresh() {
        _allActionLive.value = _allActionLive.value
    }

    fun switchSortByTimeValue() {
        _isSortByTimeLive.value = !_isSortByTimeLive.value!!
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