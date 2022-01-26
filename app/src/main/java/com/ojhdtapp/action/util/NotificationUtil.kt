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
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.MainActivity
import com.ojhdtapp.action.R
import com.ojhdtapp.action.logic.NotificationActionReceiver
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.logic.model.Suggest
import java.util.HashSet

object NotificationUtil {
    val ACTION_FINISHED = "FINISHED"
    val ACTION_IGNORED = "IGNORED"

    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(BaseApplication.context)
    }

    fun sendAction(action: Action) {
        val resultSet =
            sharedPreference.getStringSet("notification_type", hashSetOf<String>("TYPE_ACTION")) as HashSet<String>
        Log.d("aaa", resultSet.toString())
        if (resultSet.contains("TYPE_ACTION")) {
            val context = BaseApplication.context
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

            // Finished Intent
            val finishedIntent = Intent().apply {
                setAction(ACTION_FINISHED)
                putExtra("action_id", action.id)
            }
            val finishedPendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    -2,
                    finishedIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

            // Ignore Intent
            val ignoredIntent = Intent().apply {
                setAction(ACTION_IGNORED)
                putExtra("action_id", action.id)
            }
            val ignoredPendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    -3,
                    ignoredIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

            // Create an explicit intent for an Activity in your app
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val futureTarget = Glide.with(context)
                .asBitmap()
                .load(action.imageUrl)
                .submit()
            val bitmap = futureTarget.get()
            val notification = NotificationCompat.Builder(context, "action")
                .setContentText(action.title)
                .setContentTitle(context.getString(R.string.notification_action_text))
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(importance)
                .addAction(
                    R.drawable.ic_outline_done_24,
                    context.getString(R.string.action_content_fab_mark),
                    finishedPendingIntent
                )
                .addAction(
                    R.drawable.ic_outline_error_outline_24,
                    context.getString(R.string.action_content_fab_ignore),
                    ignoredPendingIntent
                )
                .build()
            Glide.with(context).clear(futureTarget)
            manager.notify(action.id.toInt(), notification)
        }
    }

    fun sendSuggest(suggest: Suggest) {
        val resultSet =
            sharedPreference.getStringSet("notification_type", setOf()) as HashSet<String>
        if (resultSet.contains("TYPE_SUGGEST")) {
            val context = BaseApplication.context
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                "suggest",
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
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val notification = NotificationCompat.Builder(context, "suggest")
                            .setContentTitle(suggest.title)
                            .setContentText(suggest.content)
                            .setSmallIcon(R.drawable.ic_stat_name)
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

    fun cancelAction(id: Int) {
        val context = BaseApplication.context
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(id)
    }
}