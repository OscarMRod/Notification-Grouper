package com.devpupu.notifgrouper.domain.model

data class GroupedApp(
    val packageName: String,
    val appName: String? = null,
    val enabled: Boolean = true
)
