package com.ojhdtapp.action.logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.R
import com.ojhdtapp.action.util.NotificationUtil

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        val actionId = intent?.getLongExtra("action_id", -1)
        Log.d("aaa", actionId.toString())
        if (action == NotificationUtil.ACTION_FINISHED && !actionId!!.equals(-1)) {
            Log.d("aaa", "action finished tap")
            NotificationUtil.cancelAction(actionId.toInt())
            Toast.makeText(BaseApplication.context, R.string.notification_finished_action_toast, Toast.LENGTH_SHORT).show()
        } else if (action == NotificationUtil.ACTION_IGNORED && !actionId!!.equals(-1)) {
            NotificationUtil.cancelAction(actionId.toInt())
            Log.d("aaa", "action ignored tap")
            Toast.makeText(BaseApplication.context, R.string.notification_ignored_action_toast, Toast.LENGTH_SHORT).show()
        }
    }
}