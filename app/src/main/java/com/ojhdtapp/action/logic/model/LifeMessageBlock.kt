package com.ojhdtapp.action.logic.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LifeMessageBlock(
    val ultravioletValue: Int,
    val ultravioletProgress: Int,
    val ComfortValue: Int,
    val ComfortProgress: Int
):Parcelable
