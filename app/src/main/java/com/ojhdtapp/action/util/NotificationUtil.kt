package com.ojhdtapp.action.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.MainActivity
import com.ojhdtapp.action.R
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.logic.model.Suggest

object NotificationUtil {
    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(BaseApplication.context)
    }

    fun sendAction(action: Action) {
        val context = BaseApplication.context
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val importance =
            when (sharedPreference.getString("notification_action", "IMPORTANCE_DEFAULT")) {
                "IMPORTANCE_DEFAULT" -> NotificationManager.IMPORTANCE_DEFAULT
                "IMPORTANCE_MIN" -> NotificationManager.IMPORTANCE_MIN
                else -> NotificationManager.IMPORTANCE_DEFAULT
            }
        val channel = NotificationChannel(
            "action",
            context.getString(R.string.channel_name_action),
            importance
        ).apply {
            description = context.getString(R.string.channel_name_action_des)
        }
        manager.createNotificationChannel(channel)
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, "action")
            .setContentTitle(action.title)
            .setContentText(action.content)
            .setSmallIcon(R.drawable.ic_outline_android_24)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, action.imageID))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        manager.notify(action.id.toInt(), notification)
    }

    fun sendSuggest(suggest:Suggest){
        val context = BaseApplication.context
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            "action",
            context.getString(R.string.channel_name_suggest),
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            description = context.getString(R.string.channel_name_suggest_des)
        }
        manager.createNotificationChannel(channel)
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        Glide.with(context)
            .asBitmap()
            .load(suggest.imgUrl)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val notification = NotificationCompat.Builder(context, "suggest")
                        .setContentTitle(suggest.title)
                        .setContentText(suggest.content)
                        .setSmallIcon(R.drawable.ic_outline_android_24)
                        .setLargeIcon(resource)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build()
                    manager.notify(suggest.id.toInt(), notification)
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    // this is called when imageView is cleared on lifecycle call or for
                    // some other reason.
                    // if you are referencing the bitmap somewhere else too other than this imageView
                    // clear it here as you can no longer have the bitmap
                }
            })

    }
}