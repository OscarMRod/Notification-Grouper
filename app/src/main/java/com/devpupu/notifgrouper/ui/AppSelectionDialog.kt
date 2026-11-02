package com.devpupu.notifgrouper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devpupu.notifgrouper.domain.model.GroupedApp

@Composable
fun AppSelectionDialog(
    allApps: List<GroupedApp>,
    enabledPackages: Set<String>,
    onToggleAppGrouping: (String, Boolean) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Surface {
            Column(Modifier.padding(16.dp)) {
                Text("Seleccionar apps", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Buscar") })
                Spacer(Modifier.height(8.dp))
                Row {
                    Button(onClick = onSelectAll) { Text("Seleccionar todo") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onDeselectAll) { Text("Deseleccionar todo") }
                }
                Spacer(Modifier.height(8.dp))
                val filtered = allApps.filter { it.appName?.contains(query, ignoreCase = true) == true || it.packageName.contains(query, ignoreCase = true) }
                Column {
                    filtered.forEach { app ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = app.packageName in enabledPackages,
                                onCheckedChange = { onToggleAppGrouping(app.packageName, it) }
                            )
                            Text(app.appName ?: app.packageName)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onDismiss) { Text("Cerrar") }
            }
        }
    }
}