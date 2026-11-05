package com.devpupu.notifgrouper.data.repository

import android.content.Context
import com.devpupu.notifgrouper.data.model.AppNotification
import com.devpupu.notifgrouper.data.model.AppNotificationGroup
import com.devpupu.notifgrouper.data.model.DeclutterStats
import com.devpupu.notifgrouper.service.SystemNotificationGrouper
import com.devpupu.notifgrouper.util.AppIconManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationRepository private constructor(context: Context) {
    private val appContext: Context = context.applicationContext

    private val _groupedNotifications = MutableStateFlow<List<AppNotificationGroup>>(emptyList())
    val groupedNotifications: StateFlow<List<AppNotificationGroup>> = _groupedNotifications.asStateFlow()

    private val _allActiveNotifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val allActiveNotifications: StateFlow<List<AppNotification>> = _allActiveNotifications.asStateFlow()

    fun publish(notification: AppNotification) {
        _allActiveNotifications.value = listOf(notification) + _allActiveNotifications.value.filterNot { it.id == notification.id }
        val active = _allActiveNotifications.value
        val groupedAppCount = active.map { it.packageName }.distinct().size
        val spammiest = active.groupingBy { it.packageName }.eachCount().maxByOrNull { it.value }
        _declutterStats.value = DeclutterStats(
            totalActiveNotifications = active.size,
            groupedAppCount = groupedAppCount,
            declutterPercentage = if (active.isEmpty()) 0 else (groupedAppCount * 100 / active.size).coerceAtMost(100),
            spammiestApp = spammiest?.key ?: "None",
            spammiestAppCount = spammiest?.value ?: 0,
            totalHistoryCount = _declutterStats.value.totalHistoryCount
        )
        // Extract/scale the originating app icon for notification cards and panels.
        AppIconManager.notificationIconBitmap(appContext, notification.packageName)
        val affected = _allActiveNotifications.value.filter { it.packageName == notification.packageName }
        SystemNotificationGrouper.showOrUpdateGroupNotification(
            context = appContext,
            packageName = notification.packageName,
            appName = notification.appName,
            notifications = affected,
            category = notification.category
        )
    }

    fun dismissAppGroup(packageName: String) {
        _allActiveNotifications.value = _allActiveNotifications.value.filterNot { it.packageName == packageName }
        SystemNotificationGrouper.cancelGroupNotification(appContext, packageName)
    }

    fun isGroupingEnabledFor(packageName: String): Boolean = true

    private val _declutterStats = MutableStateFlow(DeclutterStats(0, 0, 0, "None", 0, 0))
    val declutterStats: StateFlow<DeclutterStats> = _declutterStats.asStateFlow()

    fun syncAllToSystemPanel() {
        // No-op placeholder: grouping is handled by GroupingEngine/NotifGrouperListenerService
    }

    companion object {
        @Volatile
        private var instance: NotificationRepository? = null

        fun getInstance(context: Context): NotificationRepository {
            return instance ?: synchronized(this) {
                instance ?: NotificationRepository(context).also { instance = it }
            }
        }
    }
}