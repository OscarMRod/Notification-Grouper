package com.devpupu.notifgrouper.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupedAppDao {
    @Query("SELECT * FROM grouped_apps ORDER BY appName ASC, packageName ASC")
    fun getAll(): Flow<List<GroupedAppEntity>>

    @Query("SELECT * FROM grouped_apps")
    suspend fun getAllSync(): List<GroupedAppEntity>

    @Query("SELECT * FROM grouped_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getByPackage(packageName: String): GroupedAppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: GroupedAppEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(entity: GroupedAppEntity)

    @Update
    suspend fun update(entity: GroupedAppEntity)

    @Query("UPDATE grouped_apps SET enabled = :enabled WHERE packageName = :packageName")
    suspend fun setEnabled(packageName: String, enabled: Boolean)

    @Query("DELETE FROM grouped_apps WHERE packageName = :packageName")
    suspend fun delete(packageName: String)
}
