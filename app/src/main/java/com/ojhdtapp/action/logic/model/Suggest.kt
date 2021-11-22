package com.ojhdtapp.action.logic.model

import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.R
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp
import java.util.*

@Entity
@Parcelize
data class Suggest @Ignore constructor(
    var title: String = "Title",
    var subhead: String = "Subhead",
    var imgUrl: String? = null,
    var time: Date = Date(),
    var authorAvatarUrl: String? = null,
    var author: String = "Author",
    var source: String = "Source",
//    var type: Int = 0,
    var content: String = BaseApplication.context.getString(R.string.lorem_ipsum),
    var label: List<Pair<Int?, String>> = listOf()
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
