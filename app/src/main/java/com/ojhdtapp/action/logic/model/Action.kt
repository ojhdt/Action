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
    var label: Map<Int, String>? = emptyMap(),
    var hightlight: List<String> = emptyList<String>(),
    var history: List<ActionHistory> = emptyList<ActionHistory>(),
    var isActivating: Boolean = false,
    var lastTriggered: Long = 0,
    var weight: Int = 1,
    var activityStateTrigger: Int = -1,
    var lightStateTrigger: Int = -1,
    var locationStateTrigger: Int = -1,
    var timeStateTrigger: Int = -1,
    var weatherStateTrigger: Int = -1,
    var canSaveWater: Float = 0f,
    var canSaveElectricity: Float = 0f,
    var canSaveTree: Float = 0f,
    var objectId: String = ""
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    constructor() : this(
        "Title",
        0,
        "Content",
        emptyMap(),
        emptyList(),
        emptyList(),
        false,
        0,
        1,
        -1,
        -1,
        -1,
        -1,
        -1,
        0f,
        0f,
        0f,
        ""
    )
}

@Parcelize
data class ActionHistory(
    var time: Date = Date(),
    var cause: String = "Cause",
    var finished: Boolean = false,
) : Parcelable