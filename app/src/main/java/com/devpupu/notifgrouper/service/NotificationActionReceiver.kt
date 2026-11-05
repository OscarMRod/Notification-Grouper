package com.devpupu.notifgrouper.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.devpupu.notifgrouper.data.repository.NotificationRepository

class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_CLEAR_APP_GROUP = "com.devpupu.notifgrouper.action.CLEAR_APP_GROUP"
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return
        if (intent.action == ACTION_CLEAR_APP_GROUP) {
            val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: return
            val repository = NotificationRepository.getInstance(context)
            repository.dismissAppGroup(packageName)
            SystemNotificationGrouper.cancelGroupNotification(context, packageName)
        }
    }
}