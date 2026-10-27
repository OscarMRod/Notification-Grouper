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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.devpupu.notifgrouper.ui.OnboardingScreen
import com.devpupu.notifgrouper.ui.SettingsScreen
import com.devpupu.notifgrouper.ui.isNotificationAccessGranted
import com.devpupu.notifgrouper.ui.theme.DevPupuTheme
import dagger.hilt.android.AndroidEntryPoint

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
                        SettingsScreen()
                    } else {
                        OnboardingScreen(onOpenSettings = {})
                    }
                }
            }
        }
    }
}