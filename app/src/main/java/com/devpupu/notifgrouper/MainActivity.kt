package com.devpupu.notifgrouper

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.MaterialTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.devpupu.notifgrouper.ui.OnboardingScreen
import com.devpupu.notifgrouper.ui.HistoryScreen
import com.devpupu.notifgrouper.ui.RulesScreen
import com.devpupu.notifgrouper.ui.SettingsScreen
import com.devpupu.notifgrouper.ui.isNotificationAccessGranted
import com.devpupu.notifgrouper.ui.theme.DevPupuTheme
import dagger.hilt.android.AndroidEntryPoint

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startForegroundService(Intent(this, ForegroundKeepAliveService::class.java))
        setContent {
            DevPupuTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var granted by remember { mutableStateOf(isNotificationAccessGranted(this@MainActivity)) }

                    val permissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { /* denial handled gracefully: settings still available */ }

                    LaunchedEffect(Unit) {
                        if (Build.VERSION.SDK_INT >= 33) {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                this@MainActivity, Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                            if (!hasPermission) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }

                    if (granted) {
                        var current by remember { mutableStateOf("settings") }
                        val drawerState = rememberDrawerState(DrawerValue.Closed)
                        val scope = rememberCoroutineScope()
                        ModalNavigationDrawer(
                            drawerState = drawerState,
                            drawerContent = {
                                ModalDrawerSheet {
                                    Text("Menú", modifier = Modifier.padding(16.dp))
                                    NavigationDrawerItem(label = { Text("Settings") }, selected = current == "settings", onClick = { current = "settings"; scope.launch { drawerState.close() } })
                                    NavigationDrawerItem(label = { Text("History") }, selected = current == "history", onClick = { current = "history"; scope.launch { drawerState.close() } })
                                    NavigationDrawerItem(label = { Text("Rules") }, selected = current == "rules", onClick = { current = "rules"; scope.launch { drawerState.close() } })
                                }
                            }
                        ) {
                            Scaffold(topBar = { TopAppBar(title = { Text("Notification Grouper") }, navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Text("☰") } }) }) { padding ->
                                Box(Modifier.fillMaxSize().padding(padding)) {
                                    when (current) {
                                        "history" -> HistoryScreen()
                                        "rules" -> RulesScreen()
                                        else -> SettingsScreen()
                                    }
                                }
                            }
                        }
                    } else {
                        OnboardingScreen(onOpenSettings = {})
                    }
                }
            }
        }
    }
}