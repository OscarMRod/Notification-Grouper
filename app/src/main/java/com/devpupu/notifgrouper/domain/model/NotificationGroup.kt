package com.devpupu.notifgrouper.domain.model

import android.service.notification.StatusBarNotification

data class NotificationGroup(
    val packageName: String,
    val notifications: List<StatusBarNotification>,
    val summaryId: Int,
) {
    val count: Int get() = notifications.size
}