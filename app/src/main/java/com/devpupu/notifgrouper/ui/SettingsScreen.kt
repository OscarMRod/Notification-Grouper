package com.devpupu.notifgrouper.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val apps by viewModel.apps.collectAsState()
    var query by remember { mutableStateOf("") }
    val filtered = apps.filter {
        query.isBlank() || (it.appName ?: it.packageName).contains(query, ignoreCase = true) || it.packageName.contains(query, ignoreCase = true)
    }
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search apps") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
        }
        items(filtered, key = { it.packageName }) { app ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(app.appName ?: app.packageName, modifier = Modifier.weight(1f))
                Switch(
                    checked = app.enabled,
                    onCheckedChange = { viewModel.toggle(app.packageName, it) }
                )
            }
        }
    }
}