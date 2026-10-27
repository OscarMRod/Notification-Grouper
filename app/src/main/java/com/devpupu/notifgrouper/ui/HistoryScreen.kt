package com.devpupu.notifgrouper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val history by viewModel.history.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(history, key = { it.id }) { h ->
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(h.appName ?: h.packageName, style = MaterialTheme.typography.labelMedium)
                Text(h.title ?: "", style = MaterialTheme.typography.bodyMedium)
                Text(h.text ?: "", style = MaterialTheme.typography.bodySmall)
                Text(SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(h.timestamp)), style = MaterialTheme.typography.labelSmall)
                HorizontalDivider()
            }
        }
    }
}