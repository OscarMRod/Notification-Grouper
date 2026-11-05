package com.devpupu.notifgrouper.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.devpupu.notifgrouper.MainActivity
import com.devpupu.notifgrouper.R
import com.devpupu.notifgrouper.data.model.AppNotification
import com.devpupu.notifgrouper.data.model.AppNotificationGroup
import com.devpupu.notifgrouper.data.model.NotificationCategory
import com.devpupu.notifgrouper.util.AppIconManager

object SystemNotificationGrouper {

    private const val TAG = "SystemNotifGrouper"
    const val CHANNEL_ID = "devpupu_group_v2"
    private const val CHANNEL_NAME = "Grouped App Notifications"
    const val NOTIFICATION_GROUP_KEY = "com.devpupu.notifgrouper.SYSTEM_APP_GROUP"
    const val SUMMARY_NOTIFICATION_ID = 99999

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Shows all grouped apps cleanly consolidated in the notification panel"
                enableLights(true)
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun showOrUpdateGroupNotification(
        context: Context,
        packageName: String,
        appName: String,
        notifications: List<AppNotification>,
        category: NotificationCategory = NotificationCategory.matchCategory(packageName)
    ) {
        if (notifications.isEmpty()) {
            cancelGroupNotification(context, packageName)
            return
        }

        createNotificationChannel(context)

        try {
            val appIconBitmap = AppIconManager.notificationIconBitmap(context, packageName)
            val count = notifications.size

            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("SELECTED_PACKAGE", packageName)
            }
            val pendingOpenIntent = PendingIntent.getActivity(
                context,
                Math.abs(packageName.hashCode()),
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val clearIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.ACTION_CLEAR_APP_GROUP
                putExtra(NotificationActionReceiver.EXTRA_PACKAGE_NAME, packageName)
            }
            val pendingClearIntent = PendingIntent.getBroadcast(
                context,
                Math.abs(packageName.hashCode()) + 1,
                clearIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            val pendingLaunchApp = launchIntent?.let {
                PendingIntent.getActivity(
                    context,
                    Math.abs(packageName.hashCode()) + 2,
                    it,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

            val inboxStyle = NotificationCompat.InboxStyle()
                .setBigContentTitle("$appName ($count alerts)")
                .setSummaryText("${category.displayName} • Grouped by devpupu")

            notifications.take(5).forEach { notif ->
                val line = if (notif.title.isNotBlank()) "${notif.title}: ${notif.text}" else notif.text
                inboxStyle.addLine(line)
            }

            if (count > 5) {
                inboxStyle.addLine("+ ${count - 5} more")
            }

            val latestText = notifications.firstOrNull()?.let {
                if (it.title.isNotBlank()) "${it.title}: ${it.text}" else it.text
            } ?: "$count unread alerts"

            val notificationId = Math.abs(packageName.hashCode())

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(appIconBitmap)
                .setContentTitle("$appName ($count)")
                .setContentText(latestText)
                .setStyle(inboxStyle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setGroup(NOTIFICATION_GROUP_KEY)
                .setAutoCancel(true)
                .setContentIntent(pendingOpenIntent)
                .addAction(
                    android.R.drawable.ic_menu_close_clear_cancel,
                    "Clear Stack",
                    pendingClearIntent
                )

            pendingLaunchApp?.let {
                builder.addAction(
                    android.R.drawable.ic_menu_send,
                    "Open App",
                    it
                )
            }

            val notificationManager = NotificationManagerCompat.from(context)
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(packageName, notificationId, builder.build())
                Log.d(TAG, "Posted grouped notification in panel for $appName ($packageName)")
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "Notification permission not granted for system shade", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to post grouped notification for $packageName", e)
        }
    }

    fun updateSystemGroupSummary(context: Context, groups: List<AppNotificationGroup>) {
        if (groups.isEmpty()) {
            cancelGroupSummary(context)
            return
        }

        createNotificationChannel(context)

        try {
            val totalAlerts = groups.sumOf { it.notifications.size }
            val appCount = groups.size

            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingOpenIntent = PendingIntent.getActivity(
                context,
                SUMMARY_NOTIFICATION_ID,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val summaryInbox = NotificationCompat.InboxStyle()
                .setBigContentTitle("devpupu • $appCount Apps Grouped")
                .setSummaryText("$totalAlerts total alerts organized")

            groups.forEach { group ->
                summaryInbox.addLine("• ${group.appName}: ${group.notifications.size} alerts")
            }

            val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("devpupu • $appCount Apps Grouped")
                .setContentText("$totalAlerts alerts neatly stacked into $appCount apps")
                .setStyle(summaryInbox)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup(NOTIFICATION_GROUP_KEY)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setContentIntent(pendingOpenIntent)
                .build()

            val notificationManager = NotificationManagerCompat.from(context)
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(SUMMARY_NOTIFICATION_ID, summaryNotification)
                Log.d(TAG, "Posted master grouped apps summary to system notification panel")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to post summary notification", e)
        }
    }

    fun postAllGroupedAppsToPanel(context: Context, groups: List<AppNotificationGroup>) {
        createNotificationChannel(context)
        for (group in groups) {
            showOrUpdateGroupNotification(
                context = context,
                packageName = group.packageName,
                appName = group.appName,
                notifications = group.notifications,
                category = group.category
            )
        }
        updateSystemGroupSummary(context, groups)
    }

    fun cancelGroupNotification(context: Context, packageName: String) {
        try {
            val notificationManager = NotificationManagerCompat.from(context)
            val notificationId = Math.abs(packageName.hashCode())
            notificationManager.cancel(packageName, notificationId)
            Log.d(TAG, "Cancelled grouped notification for $packageName")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel grouped notification for $packageName", e)
        }
    }

    fun cancelGroupSummary(context: Context) {
        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(SUMMARY_NOTIFICATION_ID)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel summary", e)
        }
    }

    fun cancelAllGroupNotifications(context: Context, packageNames: Collection<String>) {
        val notificationManager = NotificationManagerCompat.from(context)
        for (pkg in packageNames) {
            val notificationId = Math.abs(pkg.hashCode())
            notificationManager.cancel(pkg, notificationId)
        }
        cancelGroupSummary(context)
    }
}