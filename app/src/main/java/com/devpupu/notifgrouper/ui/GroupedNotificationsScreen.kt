package com.devpupu.notifgrouper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GroupedNotificationsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Notificaciones agrupadas", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text("Usa el menú lateral para navegar.")
    }
}