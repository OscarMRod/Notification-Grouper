package com.devpupu.notifgrouper.data.model

data class DeclutterStats(
    val totalActiveNotifications: Int,
    val groupedAppCount: Int,
    val declutterPercentage: Int,
    val spammiestApp: String,
    val spammiestAppCount: Int,
    val totalHistoryCount: Int,
    val savedScreenSpacePx: Int = totalActiveNotifications * 72
)