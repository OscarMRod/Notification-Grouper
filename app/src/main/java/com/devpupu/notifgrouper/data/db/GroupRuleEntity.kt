package com.devpupu.notifgrouper.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_rules")
data class GroupRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val keyword: String,
    val groupKey: String
)