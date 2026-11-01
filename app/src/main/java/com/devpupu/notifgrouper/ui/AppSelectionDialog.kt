package com.devpupu.notifgrouper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun AppSelectionDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface {
            Column(Modifier.padding(16.dp)) {
                Text("Seleccionar apps")
                Spacer(Modifier.height(8.dp))
                Button(onClick = onConfirm) { Text("Confirmar") }
            }
        }
    }
}