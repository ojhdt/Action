package com.ojhdtapp.action.logic.model

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Action(val title:String, val image:Int, val ) : Parcelable
