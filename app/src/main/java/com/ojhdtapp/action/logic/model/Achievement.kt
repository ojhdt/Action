package com.ojhdtapp.action.logic.model

import android.graphics.drawable.AdaptiveIconDrawable
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ojhdtapp.action.R
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Entity
@Parcelize
data class Achievement(
    val title: String = "Title",
    val description: String = "Description",
    val type:String = "Type",
    val drawableID:Int = R.drawable.ic_outline_emoji_events_24,
    val xp: Int = 0,
    val gained: Boolean = false,
    val timestamp: Timestamp = Timestamp(0)
): Parcelable{
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
