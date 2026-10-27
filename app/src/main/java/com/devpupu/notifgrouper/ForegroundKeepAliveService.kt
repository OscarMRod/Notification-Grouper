package com.devpupu.notifgrouper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ForegroundKeepAliveService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel("devpupu_keepalive", "Notification Grouper Keep-Alive", NotificationManager.IMPORTANCE_LOW)
            nm.createNotificationChannel(channel)
        }
        val notif: Notification = NotificationCompat.Builder(this, "devpupu_keepalive")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Notification Grouper")
            .setContentText("Servicio activo para agrupar notificaciones")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        startForeground(9999, notif)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onDestroy() {
        super.onDestroy()
        // Restart
        val i = Intent(this, ForegroundKeepAliveService::class.java)
        startService(i)
    }
}