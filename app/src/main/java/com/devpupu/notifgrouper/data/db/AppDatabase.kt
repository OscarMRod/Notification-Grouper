package com.devpupu.notifgrouper.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GroupedAppEntity::class, NotificationHistoryEntity::class, GroupRuleEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupedAppDao(): GroupedAppDao
    abstract fun notificationHistoryDao(): NotificationHistoryDao
    abstract fun groupRuleDao(): GroupRuleDao
}
