package com.devpupu.notifgrouper.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_history")
data class NotificationHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appName: String?,
    val title: String?,
    val text: String?,
    val timestamp: Long
)