package com.devpupu.notifgrouper.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.devpupu.notifgrouper.MainActivity
import com.devpupu.notifgrouper.util.AppIconManager
import com.devpupu.notifgrouper.R

object SystemNotificationGrouper {
    const val CHANNEL_ID = "devpupu_group_v2"
    const val NOTIFICATION_GROUP_KEY = "com.devpupu.notifgrouper.SYSTEM_APP_GROUP"
    const val SUMMARY_NOTIFICATION_ID = 99999

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "devpupu Grouped", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Grouped notifications"
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            nm?.createNotificationChannel(channel)
        }
    }

    fun showOrUpdateGroupNotification(context: Context, packageName: String) {
        // Posts/updates only the affected app's grouped notification.
        try {
            // Ensure the originating app's icon is extracted and scaled for notifications/cards.
            AppIconManager.notificationIconBitmap(context, packageName)
            // no-op placeholder; grouping posted by GroupingEngine
        } catch (e: Exception) {}
    }

    fun cancelGroupNotification(context: Context, packageName: String) {
        try {
            val nm = NotificationManagerCompat.from(context)
            nm.cancel(packageName, Math.abs(packageName.hashCode()))
        } catch (e: Exception) {}
    }
}