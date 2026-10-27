package com.devpupu.notifgrouper.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupRuleDao {
    @Insert
    suspend fun insert(rule: GroupRuleEntity)

    @Delete
    suspend fun delete(rule: GroupRuleEntity)

    @Query("SELECT * FROM group_rules")
    fun getAll(): Flow<List<GroupRuleEntity>>

    @Query("SELECT * FROM group_rules")
    suspend fun getAllSync(): List<GroupRuleEntity>
}