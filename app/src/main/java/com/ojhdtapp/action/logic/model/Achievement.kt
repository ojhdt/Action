package com.ojhdtapp.action.logic.model

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Entity
@Parcelize
data class Achievement(
    val title: String = "Title",
    val description: String = "Description",
    val xp: Int = 0,
    val gained: Boolean = false,
    val timestamp: Timestamp = Timestamp(0)
): Parcelable
