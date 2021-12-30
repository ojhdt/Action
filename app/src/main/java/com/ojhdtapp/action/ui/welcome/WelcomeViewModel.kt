package com.ojhdtapp.action.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WelcomeViewModel:ViewModel() {
    private val _permissionLive = MutableLiveData<List<PermissionMessage>>()
    val permissionLive:LiveData<List<PermissionMessage>> get() = _permissionLive
}