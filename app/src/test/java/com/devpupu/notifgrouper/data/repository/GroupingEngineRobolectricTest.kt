package com.devpupu.notifgrouper.data.repository

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.service.notification.StatusBarNotification
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GroupingEngineRobolectricTest {
    @Test
    fun smoke() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val engine = GroupingEngine(ctx, nm)
        assertTrue(true)
    }
}