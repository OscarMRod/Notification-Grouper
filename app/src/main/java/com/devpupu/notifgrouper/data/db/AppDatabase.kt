package com.devpupu.notifgrouper.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GroupedAppEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupedAppDao(): GroupedAppDao
}
