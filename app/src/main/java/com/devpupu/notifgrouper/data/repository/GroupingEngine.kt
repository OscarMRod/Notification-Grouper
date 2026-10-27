package com.devpupu.notifgrouper.data.repository

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.devpupu.notifgrouper.domain.model.NotificationGroup
import com.devpupu.notifgrouper.DevLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class GroupingEngine(
    private val context: Context,
    private val notificationManager: NotificationManager,
) {
    private val groups = ConcurrentHashMap<String, MutableList<StatusBarNotification>>()
    private val mutex = Mutex()
    private val _groupsFlow = MutableStateFlow<Map<String, NotificationGroup>>(emptyMap())
    val groupsFlow: Flow<Map<String, NotificationGroup>> = _groupsFlow.asStateFlow()

    // summary notification id per package (stable)
    private fun summaryId(packageName: String): Int = (packageName.hashCode().toLong() and 0x7FFFFFFFL).toInt() + 1000

    suspend fun add(packageName: String, sbn: StatusBarNotification) = mutex.withLock {
        if (SensitivePackages.isExcluded(packageName)) {
            DevLog.log(context, "Engine", "add excluded pkg=$packageName")
            return@withLock
        }
        val visibility = sbn.notification?.visibility ?: android.app.Notification.VISIBILITY_PUBLIC
        if (visibility == android.app.Notification.VISIBILITY_SECRET) {
            DevLog.log(context, "Engine", "add sensitive visibility pkg=$packageName")
            return@withLock
        }
        val list = groups.getOrPut(packageName) { mutableListOf() }
        if (list.none { it.key == sbn.key }) list.add(sbn)
        publishLocked()
        DevLog.log(context, "Engine", "add posting summary pkg=$packageName count=${list.size}")
        postSummaryLocked(packageName, list)
        DevLog.log(context, "Engine", "add summary posted pkg=$packageName")
    }

    fun isExcluded(pkg: String): Boolean = SensitivePackages.isExcluded(pkg)

    suspend fun remove(packageName: String, key: String) = mutex.withLock {
        val list = groups[packageName] ?: return@withLock
        list.removeAll { it.key == key }
        if (list.isEmpty()) {
            groups.remove(packageName)
            cancelSummary(packageName)
        } else {
            postSummaryLocked(packageName, list)
        }
        publishLocked()
    }

    suspend fun clear(packageName: String) = mutex.withLock {
        groups.remove(packageName)
        cancelSummary(packageName)
        publishLocked()
    }

    suspend fun clearBySummary(sbn: StatusBarNotification) {
        val pkg = sbn.notification?.extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        if (pkg != null) clear(pkg)
    }

    suspend fun clearAll() = mutex.withLock {
        val packages = groups.keys.toList()
        groups.clear()
        packages.forEach { cancelSummary(it) }
        publishLocked()
    }

    fun snapshot(): Map<String, List<StatusBarNotification>> =
        groups.mapValues { it.value.toList() }

    private fun publishLocked() {
        val map = groups.mapValues { (pkg, list) ->
            NotificationGroup(pkg, list.toList(), summaryId(pkg))
        }
        _groupsFlow.value = map
    }

    private fun postSummaryLocked(packageName: String, list: List<StatusBarNotification>) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel("devpupu_group_v2", "devpupu Grouped", android.app.NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        val appIcon = try { context.packageManager.getApplicationIcon(packageName) } catch (e: Exception) { null }
        val appIconBitmap = appIcon?.let { drawable ->
            val bmp = android.graphics.Bitmap.createBitmap(
                drawable.intrinsicWidth.coerceAtLeast(1),
                drawable.intrinsicHeight.coerceAtLeast(1),
                android.graphics.Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(bmp)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bmp
        }
        val appLabel = try { context.packageManager.getApplicationLabel(context.packageManager.getApplicationInfo(packageName, 0)).toString() } catch (e: Exception) { packageName }
        val inboxStyle = NotificationCompat.InboxStyle()
            .setSummaryText(appLabel)
        list.forEach { sbn ->
            val title = sbn.notification?.extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString()
                ?: sbn.packageName
            inboxStyle.addLine(title ?: "")
        }
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        val contentIntent = launchIntent?.let { PendingIntent.getActivity(context, summaryId(packageName), it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE) }
        val builder = NotificationCompat.Builder(context, "devpupu_group_v2")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(appLabel)
            .setContentText("${list.size} notifications")
            .setNumber(list.size)
            .setStyle(inboxStyle)
            .setLargeIcon(appIconBitmap)
            .setGroup(packageName)
            .setGroupSummary(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
        if (contentIntent != null) builder.setContentIntent(contentIntent)
        notificationManager.notify(summaryId(packageName), builder.build())
    }

    private fun cancelSummary(packageName: String) {
        notificationManager.cancel(summaryId(packageName))
    }
}