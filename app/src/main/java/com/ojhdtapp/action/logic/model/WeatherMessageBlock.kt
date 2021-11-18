package com.ojhdtapp.action.logic.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherMessageBlock(
    val drawableID: Int,
    val title: String = "Title",
    val num: Int = 0,
    val progress: Int = 0
):Parcelable
