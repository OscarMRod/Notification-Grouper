package com.devpupu.notifgrouper.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionPromptBanner(onGrant: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("Se necesita permiso de notificaciones")
            Button(onClick = onGrant) { Text("Conceder") }
        }
    }
}