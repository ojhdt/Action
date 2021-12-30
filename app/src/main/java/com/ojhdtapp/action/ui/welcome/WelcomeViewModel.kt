package com.ojhdtapp.action.ui.welcome

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ojhdtapp.action.R

class WelcomeViewModel(application: Application) :AndroidViewModel(application) {
    private val _permissionLive = MutableLiveData<List<PermissionMessage>>()
    init {
        _permissionLive.value = mutableListOf<PermissionMessage>(
            PermissionMessage(application.getString(R.string.welcome_permission_location),
            application.getString(R.string.welcome_permission_location_summary),
            R.drawable.ic_outline_location_on_24,
            ContextCompat.checkSelfPermission(application,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        , PermissionMessage(application.getString(R.string.welcome_permission_sensor),
                application.getString(R.string.welcome_permission_sensor_summary),
                R.drawable.ic_outline_sensors_24,
                ContextCompat.checkSelfPermission(application,Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED)
        )
    }
    val permissionLive:LiveData<List<PermissionMessage>> get() = _permissionLive

    fun updateState(){
        _permissionLive.value = _permissionLive.value!!.apply {
            get(0).isGranted = ContextCompat.checkSelfPermission(getApplication(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            get(1).isGranted = ContextCompat.checkSelfPermission(getApplication(),Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED
        }
    }
}