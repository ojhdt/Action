package com.ojhdtapp.action.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.bumptech.glide.Glide
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.R
import com.ojhdtapp.action.getUriToDrawable

class AvatarPreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs) {
    @SuppressLint("CutPasteId")
    lateinit var imageView: ImageView
    private val sharedPreference: SharedPreferences by lazy {
            PreferenceManager.getDefaultSharedPreferences(context)
    }
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val imgURI = Uri.parse(
            sharedPreference.getString(
                "userAvatarURI",
                getUriToDrawable(R.drawable.avatar_a).toString()
            )
        )
        imageView = holder.findViewById(R.id.preferenceAvatar) as ImageView
        Glide.with(context)
            .load(imgURI)
            .into(imageView)
    }

    fun setAvatar(uri: Uri){
        Glide.with(context)
            .load(uri)
            .into(imageView)
    }

    fun updateAvatar(){
        val imgURI = Uri.parse(
            sharedPreference.getString(
                "userAvatarURI",
                getUriToDrawable(R.drawable.avatar_a).toString()
            )
        )
        Glide.with(context)
            .load(imgURI)
            .into(imageView)
    }
}