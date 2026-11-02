package com.devpupu.notifgrouper.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devpupu.notifgrouper.data.model.AppNotification
import com.devpupu.notifgrouper.data.model.AppNotificationGroup

@Composable
fun AppGroupCard(
    group: AppNotificationGroup,
    onToggleExpand: () -> Unit,
    onDismissNotification: (AppNotification) -> Unit,
    onDismissAppGroup: () -> Unit,
    onOpenApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(group.appName, style = MaterialTheme.typography.titleMedium)
                Text("${group.totalCount} notificaciones", style = MaterialTheme.typography.bodySmall)
            }
            if (group.isExpanded) {
                Spacer(Modifier.height(8.dp))
                group.notifications.forEach { notification ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(notification.title, style = MaterialTheme.typography.bodyMedium)
                            Text(notification.displaySnippet, style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = { onDismissNotification(notification) }) {
                            Text("Dismiss")
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onToggleExpand) { Text(if (group.isExpanded) "Collapse" else "Expand") }
                TextButton(onClick = onDismissAppGroup) { Text("Dismiss all") }
                TextButton(onClick = onOpenApp) { Text("Open") }
            }
        }
    }
}