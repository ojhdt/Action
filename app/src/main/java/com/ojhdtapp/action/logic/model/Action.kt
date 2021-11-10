package com.ojhdtapp.action.logic.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Action(val title: String = "Title", val imageID: Int = 0, val content: String="Content", val label:List<Pair<Int, String>> = listOf()) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
