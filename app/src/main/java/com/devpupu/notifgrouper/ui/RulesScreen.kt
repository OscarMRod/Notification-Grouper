package com.devpupu.notifgrouper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun RulesScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    Text("Reglas de categoría (próximamente)", modifier = Modifier.padding(16.dp))
}