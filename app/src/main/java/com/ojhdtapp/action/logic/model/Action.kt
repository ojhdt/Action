package com.ojhdtapp.action.logic.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Action(val title: String, val imageID: Int, val content: String, val label:List<Pair<Int, String>>) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
