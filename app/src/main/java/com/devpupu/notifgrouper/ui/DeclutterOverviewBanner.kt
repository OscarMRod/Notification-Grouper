package com.devpupu.notifgrouper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DeclutterOverviewBanner(count: Int, onClear: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Notificaciones agrupadas: $count")
            TextButton(onClick = onClear) { Text("Limpiar") }
        }
    }
}