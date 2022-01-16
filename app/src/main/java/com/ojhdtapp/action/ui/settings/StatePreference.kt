package com.ojhdtapp.action.ui.settings

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.bumptech.glide.Glide
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.R

class StatePreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs) {
    lateinit var imageView: ImageView
    lateinit var preferenceKey: String
    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(BaseApplication.context)
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.StatePreference,
            0, 0
        ).apply {
            try {
                preferenceKey = when (getString(R.styleable.StatePreference_type)) {
                    "awareness" -> "isAwarenessRegistered"
                    "transition" -> "isTransitionRegistered"
                    "state_accelerometer" -> "isAccelerometerSensorRegistered"
                    else -> ""
                }
            } finally {
                recycle()
            }
        }
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        imageView = holder?.findViewById(R.id.preferenceServiceState) as ImageView
        refreshState()
    }

    fun refreshState() {
        val isRegistered = sharedPreference.getBoolean(preferenceKey, true)
        Glide.with(context)
            .load(
                if (isRegistered) {
                    R.drawable.ic_outline_check_circle_outline_24
                } else R.drawable.ic_outline_error_outline_24
            ).into(imageView)
        imageView.run {
            if (isRegistered) {
                TypedValue().apply {
                    context.theme.resolveAttribute(
                        android.R.attr.colorPrimary,
                        this,
                        true
                    )
                }.data.let {
                    setColorFilter(
                        it,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            } else {
                TypedValue().apply {
                    context.theme.resolveAttribute(
                        android.R.attr.colorError,
                        this,
                        true
                    )
                }.data.let {
                    setColorFilter(
                        it,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            }
        }
    }
}