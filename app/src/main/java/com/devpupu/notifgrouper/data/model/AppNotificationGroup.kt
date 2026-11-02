package com.devpupu.notifgrouper.data.model

data class AppNotificationGroup(
    val packageName: String,
    val appName: String,
    val category: NotificationCategory,
    val notifications: List<AppNotification>,
    val latestTimestamp: Long = notifications.maxOfOrNull { it.timestamp } ?: 0L,
    val unreadCount: Int = notifications.size,
    val isExpanded: Boolean = true
) {
    val totalCount: Int get() = notifications.size
    val latestSnippet: String
        get() = notifications.firstOrNull()?.let {
            if (it.title.isNotBlank() && it.text.isNotBlank()) {
                "${it.title}: ${it.text}"
            } else {
                it.title.ifBlank { it.text }
            }
        } ?: "No notifications"
}