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
import com.devpupu.notifgrouper.data.repository.AppPreferencesRepository
import com.devpupu.notifgrouper.data.repository.GroupingEngine
import com.devpupu.notifgrouper.data.repository.SensitivePackages

import java.util.concurrent.ConcurrentHashMap

@AndroidEntryPoint
class NotifGrouperListenerService : NotificationListenerService() {
    @Inject
    lateinit var groupingEngine: GroupingEngine

    @Inject
    lateinit var appPrefs: AppPreferencesRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val recentlyCancelled = ConcurrentHashMap.newKeySet<String>()
    private val recentlyPosted = ConcurrentHashMap.newKeySet<String>()
    private val postedTimes = ConcurrentHashMap<String, Long>()

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) {
            DevLog.log(applicationContext, "Listener", "onNotificationPosted: null sbn")
            return
        }
        if (sbn.packageName == packageName) return
        val now = System.currentTimeMillis()
        val last = postedTimes[sbn.key]
        if (last != null && now - last < 2000) return
        postedTimes[sbn.key] = now
        val skip = SensitivePackages.isExcluded(sbn.packageName) ||
            run {
                val v = sbn.notification?.visibility ?: android.app.Notification.VISIBILITY_PUBLIC
                v == android.app.Notification.VISIBILITY_SECRET
            }
        DevLog.log(applicationContext, "Listener", "posted pkg=${sbn.packageName} skip=$skip key=${sbn.key}")
        if (skip) return
        // Register app in prefs (disabled by default) if new, then group only if enabled
        scope.launch {
            val appName = try { applicationContext.packageManager.getApplicationLabel(applicationContext.packageManager.getApplicationInfo(sbn.packageName, 0)).toString() } catch (e: Exception) { null }
            appPrefs.registerIfMissing(sbn.packageName, appName)
            val enabled = try { appPrefs.isGrouped(sbn.packageName) } catch (e: Exception) { false }
            if (!enabled) return@launch
            groupingEngine.add(sbn.packageName, sbn)
            DevLog.log(applicationContext, "Listener", "add done pkg=${sbn.packageName}")
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

    override fun onListenerConnected() {
        super.onListenerConnected()
        try {
            activeNotifications?.forEach { sbn ->
                if (sbn.packageName != packageName) onNotificationPosted(sbn)
            }
        } catch (e: Exception) {
            DevLog.log(applicationContext, "Listener", "onListenerConnected error: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}