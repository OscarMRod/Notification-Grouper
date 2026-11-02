package com.devpupu.notifgrouper.data.repository

import android.content.Context
import com.devpupu.notifgrouper.data.model.AppNotification
import com.devpupu.notifgrouper.data.model.AppNotificationGroup
import com.devpupu.notifgrouper.data.model.DeclutterStats
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