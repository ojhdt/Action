package com.ojhdtapp.action.ui.welcome

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ojhdtapp.action.R

class WelcomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _permissionLive = MutableLiveData<List<PermissionMessage>>()
    private val _permissionStateLive = MutableLiveData<Map<String, Boolean>>()

    init {
        val iniState = mapOf<String, Boolean>(
            Manifest.permission.ACCESS_FINE_LOCATION to (ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED),
            Manifest.permission.BODY_SENSORS to (ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.BODY_SENSORS
            ) == PackageManager.PERMISSION_GRANTED)
        )
        _permissionLive.value = mutableListOf<PermissionMessage>(
            PermissionMessage(
                application.getString(R.string.welcome_permission_location),
                application.getString(R.string.welcome_permission_location_summary),
                R.drawable.ic_outline_location_on_24,
                iniState[Manifest.permission.ACCESS_FINE_LOCATION]?:false
            ), PermissionMessage(
                application.getString(R.string.welcome_permission_sensor),
                application.getString(R.string.welcome_permission_sensor_summary),
                R.drawable.ic_outline_sensors_24,
                iniState[Manifest.permission.BODY_SENSORS]?:false
            )
        )
        _permissionStateLive.value = iniState
    }

    val permissionLive: LiveData<List<PermissionMessage>> get() = _permissionLive
    val permissionStateLive: LiveData<Map<String,Boolean>> get() = _permissionStateLive

    fun updateState(result: Map<String, Boolean>) {
//        val list = _permissionLive.value!!.apply {
//            get(0).isGranted = result[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
//            get(1).isGranted = result[Manifest.permission.BODY_SENSORS] ?: false
//        }
//        _permissionLive.value = list
        _permissionStateLive.value = result
        val application = getApplication<Application>()
        _permissionLive.value = mutableListOf<PermissionMessage>(
            PermissionMessage(
                application.getString(R.string.welcome_permission_location),
                application.getString(R.string.welcome_permission_location_summary),
                R.drawable.ic_outline_location_on_24,
                result[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            ), PermissionMessage(
                application.getString(R.string.welcome_permission_sensor),
                application.getString(R.string.welcome_permission_sensor_summary),
                R.drawable.ic_outline_sensors_24,
                result[Manifest.permission.BODY_SENSORS] ?: false
            )
        )
    }
}