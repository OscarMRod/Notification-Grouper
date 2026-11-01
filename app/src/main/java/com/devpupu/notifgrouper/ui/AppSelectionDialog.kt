package com.devpupu.notifgrouper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AppSelectionDialog(onDismiss: () -> Unit, onConfirm: (List<String>) -> Unit) {
    var query by remember { mutableStateOf("") }
    val selected = remember { mutableStateListOf<String>() }
    Dialog(onDismissRequest = onDismiss) {
        Surface {
            Column(Modifier.padding(16.dp)) {
                Text("Seleccionar apps", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Buscar") })
                Spacer(Modifier.height(8.dp))
                Button(onClick = { onConfirm(selected.toList()); onDismiss() }) { Text("Confirmar") }
            }
        }
    }
}