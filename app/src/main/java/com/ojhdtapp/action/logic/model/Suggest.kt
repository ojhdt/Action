package com.ojhdtapp.action.logic.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Entity
@Parcelize
data class Suggest(
    val title: String = "Title",
    val subhead: String = "Subhead",
    val imgUrl: String?,
    val timestamp: Timestamp,
    val type: Int = 0,
    val content: String,
    val label: List<Pair<Int?, String>> = listOf()
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
