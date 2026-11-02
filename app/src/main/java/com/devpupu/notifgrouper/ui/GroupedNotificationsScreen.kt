package com.devpupu.notifgrouper.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devpupu.notifgrouper.data.SortOrder
import com.devpupu.notifgrouper.ui.NotifyViewModel
import com.devpupu.notifgrouper.ui.AppSelectionDialog
import com.devpupu.notifgrouper.ui.CategoryFilterChips
import com.devpupu.notifgrouper.ui.DeclutterOverviewBanner
import com.devpupu.notifgrouper.ui.components.AppGroupCard
import com.devpupu.notifgrouper.ui.components.PermissionPromptBanner

@Composable
fun GroupedNotificationsScreen(
    viewModel: NotifyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val groups by viewModel.groupedNotifications.collectAsState()
    val stats by viewModel.declutterStats.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val isPermissionGranted by viewModel.isPermissionGranted.collectAsState()
    val bannerMessage by viewModel.bannerMessage.collectAsState()
    val allConfigurableApps by viewModel.allConfigurableApps.collectAsState()
    val enabledPackages by viewModel.enabledGroupingPackages.collectAsState()
    val ungroupedAlerts by viewModel.ungroupedNotifications.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }
    var showAddSampleDialog by remember { mutableStateOf(false) }
    var showAppSelectionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.checkPermissionStatus()
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("grouped_notifications_list"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Header: App Title, Search & Sort
            item(key = "header_search") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = viewModel::onSearchQueryChange,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("search_notifications_input"),
                            placeholder = { Text("Search apps, senders, text...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotBlank()) {
                                    IconButton(
                                        onClick = { viewModel.onSearchQueryChange("") },
                                        modifier = Modifier.testTag("clear_search_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear search",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Sort Menu Trigger
                        Box {
                            IconButton(
                                onClick = { showSortMenu = true },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                    .testTag("sort_order_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Sort order",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                SortOrder.entries.forEach { order ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                if (order == sortOrder) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                Text(order.displayName)
                                            }
                                        },
                                        onClick = {
                                            viewModel.onSortOrderSelect(order)
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // App Selection Pill & Action Row
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            onClick = { showAppSelectionDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.testTag("open_app_selection_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Grouped Apps: ${enabledPackages.size} of ${allConfigurableApps.size} enabled",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }

                        TextButton(
                            onClick = { showAppSelectionDialog = true },
                            modifier = Modifier.testTag("select_apps_link_button")
                        ) {
                            Text("Select Apps", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Permission Prompt (if not granted)
            if (!isPermissionGranted) {
                item(key = "permission_banner") {
                    PermissionPromptBanner(
                        onEnableClicked = { viewModel.requestNotificationSettings() },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            // Declutter Overview Hero Card
            if (stats.totalActiveNotifications > 0) {
                item(key = "declutter_overview") {
                    DeclutterOverviewBanner(
                        count = stats.totalActiveNotifications,
                        onClear = viewModel::clearAllActive
                    )
                }
            }

            // System Notification Panel Sync Status Card
            item(key = "system_panel_sync_card") {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("system_panel_status_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Active on Notification Panel",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = "Grouped apps are posted to your Android notification shade",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        TextButton(
                            onClick = viewModel::syncToSystemShade,
                            modifier = Modifier.testTag("refresh_panel_button")
                        ) {
                            Text("Refresh", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Category Filter Chips
            item(key = "category_chips") {
                CategoryFilterChips(
                    categories = listOf(),
                    selected = null,
                    onSelect = { }
                )
            }

            // Empty State
            if (groups.isEmpty()) {
                item(key = "empty_state") {
                    EmptyGroupsState(
                        searchQuery = searchQuery,
                        hasDisabledAllApps = enabledPackages.isEmpty(),
                        onOpenAppSelection = { showAppSelectionDialog = true },
                        onReset = viewModel::resetDemoData,
                        onClearSearch = { viewModel.onSearchQueryChange("") },
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 48.dp)
                    )
                }
            } else {
                // Section Title
                item(key = "groups_title") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "APP STACKS (${groups.size})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )

                            IconButton(
                                onClick = { showAppSelectionDialog = true },
                                modifier = Modifier
                                    .size(24.dp)
                                    .testTag("app_stacks_tune_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Select apps to group",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Text(
                            text = "Sorted by: ${sortOrder.displayName}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Collapsible App Group Cards
                items(
                    items = groups,
                    key = { it.packageName }
                ) { group ->
                    AppGroupCard(
                        group = group,
                        onToggleExpand = { viewModel.toggleAppExpansion(group.packageName) },
                        onDismissNotification = viewModel::dismissNotification,
                        onDismissAppGroup = { viewModel.dismissAppGroup(group) },
                        onOpenApp = { viewModel.openApp(group.packageName, context) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            // Ungrouped Alerts Section (Bypassed apps)
            if (ungroupedAlerts.isNotEmpty()) {
                item(key = "ungrouped_apps_section") {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("ungrouped_apps_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Ungrouped Alerts (${ungroupedAlerts.size})",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    )
                                }

                                TextButton(
                                    onClick = { showAppSelectionDialog = true },
                                    modifier = Modifier.testTag("manage_ungrouped_button")
                                ) {
                                    Text("Manage", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "These alerts bypass NotifyStack grouping because their apps are switched off. They remain normal in your system shade.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            )

                            val groupedByPkg = ungroupedAlerts.groupBy { it.packageName }
                            groupedByPkg.forEach { (pkg, alerts) ->
                                val first = alerts.first()
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = first.appName,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                        Text(
                                            text = "${alerts.size} alert${if (alerts.size > 1) "s" else ""} • ${first.text}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp
                                            ),
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.setAppGroupingEnabled(pkg, true) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("quick_enable_grouping_${pkg}")
                                    ) {
                                        Text("+ Group", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to inject a simulated incoming notification for instant demo testing
        FloatingActionButton(
            onClick = { showAddSampleDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .testTag("simulate_notification_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Simulate notification"
            )
        }

        // Banner / Toast Feedback with Undo
        bannerMessage?.let { banner ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = banner.message,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    banner.actionLabel?.let { label ->
                        TextButton(onClick = { banner.onAction?.invoke() }) {
                            Text(
                                text = label,
                                color = MaterialTheme.colorScheme.inversePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Quick dialog to add sample notification
        if (showAddSampleDialog) {
            SampleNotificationDialog(
                onDismiss = { showAddSampleDialog = false },
                onSelectApp = { appType ->
                    viewModel.addSampleNotification()
                    showAddSampleDialog = false
                }
            )
        }

        // App Selection Dialog
        if (showAppSelectionDialog) {
            AppSelectionDialog(
                allApps = allConfigurableApps,
                enabledPackages = enabledPackages,
                onToggleAppGrouping = viewModel::setAppGroupingEnabled,
                onSelectAll = viewModel::selectAllAppsToGroup,
                onDeselectAll = viewModel::deselectAllAppsToGroup,
                onDismiss = { showAppSelectionDialog = false }
            )
        }
    }
}

@Composable
fun EmptyGroupsState(
    searchQuery: String,
    hasDisabledAllApps: Boolean = false,
    onOpenAppSelection: () -> Unit = {},
    onReset: () -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when {
                searchQuery.isNotBlank() -> "No notifications found"
                hasDisabledAllApps -> "All Apps Ungrouped"
                else -> "Notification Panel Clean!"
            },
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = when {
                searchQuery.isNotBlank() ->
                    "No notifications match '$searchQuery'. Try clearing the search."
                hasDisabledAllApps ->
                    "You have unselected all apps from grouping. Incoming notifications will appear as standard unbundled alerts."
                else ->
                    "No pending notifications to declutter. All incoming notifications from selected apps will automatically be grouped here."
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            searchQuery.isNotBlank() -> {
                Button(
                    onClick = onClearSearch,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Clear Search")
                }
            }
            hasDisabledAllApps -> {
                Button(
                    onClick = onOpenAppSelection,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("empty_select_apps_button")
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Select Apps to Group")
                }
            }
            else -> {
                Button(
                    onClick = onReset,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("reset_demo_data_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Load Sample Stacks")
                }
            }
        }
    }
}

@Composable
fun SampleNotificationDialog(
    onDismiss: () -> Unit,
    onSelectApp: (String) -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Simulate Incoming Notification", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Select an app to post a new notification to your grouped stack:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))

                val sampleApps = listOf(
                    "WhatsApp" to "whatsapp",
                    "Slack" to "slack",
                    "Uber Eats" to "ubereats",
                    "Instagram" to "instagram",
                    "Chase Mobile" to "chase",
                    "Gmail" to "gmail"
                )

                sampleApps.forEach { (name, key) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectApp(key) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            Text("+ Add", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
