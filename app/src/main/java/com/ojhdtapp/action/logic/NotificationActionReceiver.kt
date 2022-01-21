package com.ojhdtapp.action.logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.R
import com.ojhdtapp.action.util.NotificationUtil

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action == NotificationUtil.ACTION_FINISHED) {
            Toast.makeText(BaseApplication.context, R.string.notification_finished_action_toast, Toast.LENGTH_SHORT).show()
        } else if (action == NotificationUtil.ACTION_IGNORED) {
            Toast.makeText(BaseApplication.context, R.string.notification_ignored_action_toast, Toast.LENGTH_SHORT).show()
        }
    }
}