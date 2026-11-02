package com.devpupu.notifgrouper.data.model

data class NotificationActionItem(
    val title: String,
    val actionKey: String = ""
)

data class AppNotification(
    val id: String,
    val packageName: String,
    val appName: String,
    val title: String,
    val text: String,
    val subText: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isClearable: Boolean = true,
    val category: NotificationCategory = NotificationCategory.SYSTEM,
    val priority: Int = 0,
    val key: String = id,
    val isDemo: Boolean = false,
    val dismissedAt: Long? = null,
    val actions: List<NotificationActionItem> = emptyList()
) {
    val displaySnippet: String
        get() = if (text.isNotBlank()) text else subText ?: title
}