package com.devpupu.notifgrouper.ui

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devpupu.notifgrouper.data.SortOrder
import com.devpupu.notifgrouper.data.model.AppNotification
import com.devpupu.notifgrouper.data.model.AppNotificationGroup
import com.devpupu.notifgrouper.data.model.DeclutterStats
import com.devpupu.notifgrouper.data.model.NotificationCategory
import com.devpupu.notifgrouper.data.repository.NotificationRepository
import com.devpupu.notifgrouper.domain.model.GroupedApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.LinkedHashMap

enum class AppNavTab(val title: String) {
    GROUPS("Grouped"),
    INSIGHTS("Insights"),
    HISTORY("History"),
    SETTINGS("Settings")
}

data class UiBannerMessage(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null
)

class NotifyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NotificationRepository.getInstance(application)

    init {
        repository.syncAllToSystemPanel()
    }

    val groupedNotifications: StateFlow<List<AppNotificationGroup>> =
        repository.groupedNotifications.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val declutterStats: StateFlow<DeclutterStats> =
        repository.declutterStats.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DeclutterStats(0, 0, 0, "None", 0, 0)
        )

    private val _isPermissionGranted = MutableStateFlow(false)
    val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted.asStateFlow()

    private val _currentTab = MutableStateFlow(AppNavTab.GROUPS)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    private val _bannerMessage = MutableStateFlow<UiBannerMessage?>(null)
    val bannerMessage: StateFlow<UiBannerMessage?> = _bannerMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(NotificationCategory.ALL)
    val selectedCategory: StateFlow<NotificationCategory> = _selectedCategory.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.CATEGORY_RECENCY)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _allConfigurableApps = MutableStateFlow<List<GroupedApp>>(emptyList())
    val allConfigurableApps: StateFlow<List<GroupedApp>> = _allConfigurableApps.asStateFlow()

    private val _enabledGroupingPackages = MutableStateFlow<Set<String>>(emptySet())
    val enabledGroupingPackages: StateFlow<Set<String>> = _enabledGroupingPackages.asStateFlow()

    val ungroupedNotifications: StateFlow<List<AppNotification>> =
        repository.allActiveNotifications.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val expandedApps = MutableStateFlow<Set<String>>(emptySet())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: NotificationCategory) {
        _selectedCategory.value = category
    }

    fun onSortOrderSelect(order: SortOrder) {
        _sortOrder.value = order
    }

    fun toggleAppExpansion(packageName: String) {
        val current = expandedApps.value
        expandedApps.value = if (packageName in current) current - packageName else current + packageName
    }

    fun dismissNotification(item: AppNotification) {
        _bannerMessage.value = UiBannerMessage(
            message = "Cleared: ${item.appName} notification",
            actionLabel = "Undo",
            onAction = { _bannerMessage.value = null }
        )
    }

    fun dismissAppGroup(group: AppNotificationGroup) {
        _bannerMessage.value = UiBannerMessage(
            message = "Decluttered ${group.notifications.size} notifications from ${group.appName}",
            actionLabel = "Dismiss",
            onAction = { _bannerMessage.value = null }
        )
    }

    fun clearAllActive() {
        val count = groupedNotifications.value.sumOf { it.notifications.size }
        _bannerMessage.value = UiBannerMessage(
            message = "All $count notifications cleared! Panel is clean.",
            actionLabel = "Done",
            onAction = { _bannerMessage.value = null }
        )
    }

    fun clearBanner() {
        _bannerMessage.value = null
    }

    fun syncToSystemShade() {
        repository.syncAllToSystemPanel()
        Toast.makeText(getApplication(), "Grouped stacks synced to Android notification shade!", Toast.LENGTH_SHORT).show()
    }

    fun openApp(packageName: String, context: Context) {
        try {
            val pm = context.packageManager
            val launchIntent = pm.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
            } else {
                Toast.makeText(context, "Cannot open package: $packageName", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "App could not be launched", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkPermissionStatus() {
        _isPermissionGranted.value = true
    }

    fun requestNotificationSettings() {
        // placeholder
    }

    fun setAppGroupingEnabled(packageName: String, enabled: Boolean) {
        _enabledGroupingPackages.value = if (enabled) _enabledGroupingPackages.value + packageName else _enabledGroupingPackages.value - packageName
    }

    fun selectAllAppsToGroup() {
        _enabledGroupingPackages.value = _allConfigurableApps.value.map { it.packageName }.toSet()
    }

    fun deselectAllAppsToGroup() {
        _enabledGroupingPackages.value = emptySet()
    }

    fun expandAll() {
        expandedApps.value = groupedNotifications.value.map { it.packageName }.toSet()
    }

    fun collapseAll() {
        expandedApps.value = emptySet()
    }

    fun resetDemoData() {
        // placeholder
    }

    fun addSampleNotification() {
        // placeholder
    }

    private val _excludeSystemApps = MutableStateFlow(true)
    val excludeSystemApps: StateFlow<Boolean> = _excludeSystemApps.asStateFlow()

    fun loadConfigurableApps(context: Context, excludeSystemApps: Boolean? = null) {
        val prefs = context.getSharedPreferences("devpupu_prefs", Context.MODE_PRIVATE)
        val exclude = excludeSystemApps ?: prefs.getBoolean("exclude_system_apps", true)
        if (excludeSystemApps != null) {
            prefs.edit().putBoolean("exclude_system_apps", exclude).apply()
        }
        _excludeSystemApps.value = exclude
        viewModelScope.launch {
            val pm = context.packageManager
            val launchIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            val resolveInfos = pm.queryIntentActivities(launchIntent, PackageManager.MATCH_ALL)
            val collected = mutableMapOf<String, GroupedApp>()
            for (ri in resolveInfos) {
                val ai = ri.activityInfo.applicationInfo
                if (exclude && (ai.flags and ApplicationInfo.FLAG_SYSTEM) != 0) continue
                val label = try { pm.getApplicationLabel(ai).toString() } catch (e: Exception) { ai.packageName }
                // touch icon to ensure it resolves (real app icon)
                try { pm.getApplicationIcon(ai) } catch (e: Exception) { }
                collected[ai.packageName] = GroupedApp(packageName = ai.packageName, appName = label)
            }
            // Auto-register unlisted apps that post notifications
            for (group in repository.groupedNotifications.value) {
                val pkg = group.packageName
                if (pkg in collected) continue
                val label = try {
                    pm.getApplicationLabel(pm.getApplicationInfo(pkg, PackageManager.GET_META_DATA)).toString()
                } catch (e: Exception) { pkg }
                try { pm.getApplicationIcon(pkg) } catch (e: Exception) { }
                collected[pkg] = GroupedApp(packageName = pkg, appName = label)
            }
            _allConfigurableApps.value = collected.values.sortedBy { it.appName?.lowercase() }
        }
    }

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }
}