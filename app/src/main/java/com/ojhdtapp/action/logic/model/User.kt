package com.ojhdtapp.action.logic.model

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Entity
@Parcelize
data class User(val username: String, val avatarResID: Int) :
    Parcelable
