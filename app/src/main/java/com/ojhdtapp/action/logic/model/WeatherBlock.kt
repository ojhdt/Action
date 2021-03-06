package com.ojhdtapp.action.logic.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherBlock(
    val location: String, val skycon: String, val temperatureNow: WeatherTemperature,
    val temperature1HourLater:WeatherTemperature,
    val temperature2HoursLater: WeatherTemperature, val temperature3HoursLater: WeatherTemperature,
    val temperature4HoursLater: WeatherTemperature,
    val temperatureTomorrow: WeatherTemperature,
    val temperatureTheDayAfterTomorrow: WeatherTemperature,
):Parcelable{
    @Parcelize
    data class WeatherTemperature(val rawID:Int, val value:Int, val lowest:Int = 0, val highest:Int = 0):Parcelable
}

