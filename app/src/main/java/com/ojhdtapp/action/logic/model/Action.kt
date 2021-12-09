package com.ojhdtapp.action.logic.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.ojhdtapp.action.R
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp
import java.util.*

@Parcelize
@Entity(tableName = "action_table")
data class Action @Ignore constructor(
    var title: String = "Title",
    var imageID: Int = 0,
    var content: String = "Content",
    var label: Map<Int, String>? = mapOf(),
    var hightlight: List<String> = listOf(),
    var history: List<ActionHistory> = listOf(),
    var isActivating: Boolean = false
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    constructor() : this("Title", 0, "Content", mapOf(), listOf(), listOf(), false)
}

@Parcelize
data class ActionHistory(
    var time: Date = Date(),
    var source: String = "Source",
    var finished: Boolean = false
) : Parcelable