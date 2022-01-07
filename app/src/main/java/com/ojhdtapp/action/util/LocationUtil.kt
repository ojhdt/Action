package com.ojhdtapp.action.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.ojhdtapp.action.BaseApplication

object LocationUtil {
    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        val context = BaseApplication.context
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: getLocationByNetwork()
        } else {
            getLocationByNetwork()
        }
        return location
    }

    @SuppressLint("MissingPermission")
    fun getLocationByNetwork(): Location? {
        val context = BaseApplication.context
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } else null
    }
}