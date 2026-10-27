package com.devpupu.notifgrouper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RulesScreen(viewModel: RulesViewModel = hiltViewModel()) {
    val rules by viewModel.rules.collectAsState()
    var name by remember { mutableStateOf("") }
    var keyword by remember { mutableStateOf("") }
    var groupKey by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Reglas de categoría", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
        OutlinedTextField(value = keyword, onValueChange = { keyword = it }, label = { Text("Palabra clave") })
        OutlinedTextField(value = groupKey, onValueChange = { groupKey = it }, label = { Text("Clave de grupo") })
        Button(onClick = { if (name.isNotBlank() && keyword.isNotBlank()) { viewModel.add(name, keyword, groupKey.ifBlank { name }); name = ""; keyword = ""; groupKey = "" } }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Agregar")
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(rules, key = { it.id }) { r ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(r.name, style = MaterialTheme.typography.bodyMedium)
                        Text("kw: ${r.keyword} → ${r.groupKey}", style = MaterialTheme.typography.bodySmall)
                    }
                    TextButton(onClick = { viewModel.remove(r) }) { Text("Eliminar") }
                }
            }
        }
    }
}