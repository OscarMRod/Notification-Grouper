package com.devpupu.notifgrouper.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grouped_apps")
data class GroupedAppEntity(
    @PrimaryKey val packageName: String,
    val appName: String? = null,
    val enabled: Boolean = true
)
