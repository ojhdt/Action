package com.ojhdtapp.action.ui.settings

import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.bumptech.glide.Glide
import com.ojhdtapp.action.R
import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.Q)
class PermissionPreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs) {
    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PermissionPreference,
            0, 0
        ).apply {
            try {
                permission = when(getString(R.styleable.PermissionPreference_permission)!!){
                    "location" -> Manifest.permission.ACCESS_FINE_LOCATION
                    "sensor" -> Manifest.permission.BODY_SENSORS
                    "storage" -> Manifest.permission.READ_EXTERNAL_STORAGE
                    "activity_recognition" -> Manifest.permission.ACTIVITY_RECOGNITION
                    else -> ""
                }
            } finally {
                recycle()
            }
        }
    }

    lateinit var imageView: ImageView
    lateinit var permission: String
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        imageView = holder.findViewById(R.id.preferencePermissionState) as ImageView
        refreshState()
    }

    fun refreshState() {
        val isGranted = (ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED)
        Glide.with(context)
            .load(
                if (isGranted) {
                    R.drawable.ic_outline_check_circle_outline_24
                } else R.drawable.ic_outline_error_outline_24
            ).into(imageView)
        imageView.run {
            if (isGranted) {
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