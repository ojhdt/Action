package com.ojhdtapp.action.logic.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Parcelize
@Entity
data class Action(
    val title: String = "Title",
    val imageID: Int = 0,
    val content: String = "Content",
    val source: String = "Source",
    val timestamp: Timestamp,
    val label: List<Pair<Int?, String>> = listOf()
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
