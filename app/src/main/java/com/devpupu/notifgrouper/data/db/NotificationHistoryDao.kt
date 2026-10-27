package com.devpupu.notifgrouper.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationHistoryDao {
    @Insert
    suspend fun insert(entity: NotificationHistoryEntity)

    @Query("SELECT * FROM notification_history ORDER BY timestamp DESC LIMIT 200")
    fun getAll(): Flow<List<NotificationHistoryEntity>>

    @Query("SELECT * FROM notification_history WHERE title LIKE '%' || :q || '%' OR text LIKE '%' || :q || '%' OR packageName LIKE '%' || :q || '%' ORDER BY timestamp DESC LIMIT 200")
    fun search(q: String): Flow<List<NotificationHistoryEntity>>

    @Query("DELETE FROM notification_history")
    suspend fun clearAll()
}