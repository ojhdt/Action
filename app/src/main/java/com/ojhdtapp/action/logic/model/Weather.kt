package com.ojhdtapp.action.logic.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Weather(
    val weather: WeatherBlock,
    val air: WeatherMessageBlock,
    val life: LifeMessageBlock,
    val isTemp:Boolean = false
) : Parcelable