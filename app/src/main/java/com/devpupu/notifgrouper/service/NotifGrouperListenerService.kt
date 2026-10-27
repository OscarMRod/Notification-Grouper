package com.devpupu.notifgrouper.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import com.devpupu.notifgrouper.DevLog
import com.devpupu.notifgrouper.data.repository.GroupingEngine
import com.devpupu.notifgrouper.data.repository.SensitivePackages

import java.util.concurrent.ConcurrentHashMap

@AndroidEntryPoint
class NotifGrouperListenerService : NotificationListenerService() {
    @Inject
    lateinit var groupingEngine: GroupingEngine

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val recentlyCancelled = ConcurrentHashMap.newKeySet<String>()

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) {
            DevLog.log(applicationContext, "Listener", "onNotificationPosted: null sbn")
            return
        }
        if (sbn.packageName == packageName) return
        val skip = SensitivePackages.isExcluded(sbn.packageName) ||
            run {
                val v = sbn.notification?.visibility ?: android.app.Notification.VISIBILITY_PUBLIC
                v == android.app.Notification.VISIBILITY_SECRET
            }
        DevLog.log(applicationContext, "Listener", "posted pkg=${sbn.packageName} skip=$skip key=${sbn.key}")
        if (skip) return
        scope.launch {
            groupingEngine.add(sbn.packageName, sbn)
            DevLog.log(applicationContext, "Listener", "add done pkg=${sbn.packageName}")
            try {
                // Repost original as a silent, low-priority notification so it stays
                // accessible/clickable in the shade without alerting.
                val nm = applicationContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    val ch = android.app.NotificationChannel("devpupu_silent", "devpupu Silent", android.app.NotificationManager.IMPORTANCE_LOW)
                    ch.setSound(null, null)
                    ch.enableVibration(false)
                    nm.createNotificationChannel(ch)
                }
                val orig = sbn.notification
                if (orig != null) {
                    val silent = androidx.core.app.NotificationCompat.Builder(applicationContext, "devpupu_silent")
                        .setSmallIcon(orig.smallIcon?.resId?.takeIf { it != 0 } ?: android.R.drawable.ic_dialog_info)
                        .setContentTitle(orig.extras?.getCharSequence(android.app.Notification.EXTRA_TITLE))
                        .setContentText(orig.extras?.getCharSequence(android.app.Notification.EXTRA_TEXT))
                        .setContentIntent(orig.contentIntent)
                        .setAutoCancel(true)
                        .setSilent(true)
                        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_LOW)
                        .build()
                    nm.notify(sbn.id, silent)
                }
                recentlyCancelled.add(sbn.key)
                // Cancel the loud original
                cancelNotification(sbn.key)
                DevLog.log(applicationContext, "Listener", "reposted silent + cancelled original pkg=${sbn.packageName}")
            } catch (e: Exception) {
                DevLog.log(applicationContext, "Listener", "silent repost failed: ${e.message}")
                try { cancelNotification(sbn.key) } catch (_: Exception) {}
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        if (sbn == null) return
        if (sbn.packageName == packageName) {
            // Only treat summary-channel notifications as dismissals.
            val channel = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) sbn.notification?.channelId else null
            if (channel == "devpupu_group_v2") {
                scope.launch { groupingEngine.clearBySummary(sbn) }
            }
            return
        }
        // Skip removals triggered by our own cancellation (original was already grouped)
        if (recentlyCancelled.remove(sbn.key)) return
        scope.launch {
            groupingEngine.remove(sbn.packageName, sbn.key)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}