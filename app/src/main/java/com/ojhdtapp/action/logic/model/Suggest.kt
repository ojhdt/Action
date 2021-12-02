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

@Parcelize
@Entity(tableName = "suggest_table")
data class Suggest @Ignore constructor(
    var title: String = "Title",
    var subhead: String = "Subhead",
    var imgUrl: String? = null,
    var time: Date = Date(),
    var authorAvatarUrl: String? = null,
    var author: String = "Author",
    var source: String = "Source",
    var type: Int = 0,
    var content: String = BaseApplication.context.getString(R.string.lorem_ipsum),
    var label: Map<Int,String>? = mapOf(),
    var sourceUrl:String? = null,
    var like:Int = 0,
    var dislike:Int = 0,
    var archived: Boolean = false,
    var read: Boolean = false,
    var objectId: String? = null
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    constructor() : this(
        "Title",
        "Sunhead",
        null,
        Date(),
        null,
        "Author",
        "Source",
        1,
        BaseApplication.context.getString(R.string.lorem_ipsum),
        mapOf(),
        null,
        0,
        0,
        false,
        false,
    )
}
