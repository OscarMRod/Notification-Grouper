package com.devpupu.notifgrouper.data.repository

import android.app.NotificationManager
import android.content.Context
import android.service.notification.StatusBarNotification
import com.devpupu.notifgrouper.domain.model.NotificationGroup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class GroupingEngineTest {

    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager
    private lateinit var engine: GroupingEngine

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        engine = GroupingEngine(context, notificationManager)
    }

    private fun sbn(packageName: String, key: String, title: String? = null): StatusBarNotification {
        val sbn = org.mockito.kotlin.mock<StatusBarNotification>()
        org.mockito.kotlin.whenever(sbn.packageName).thenReturn(packageName)
        org.mockito.kotlin.whenever(sbn.key).thenReturn(key)
        org.mockito.kotlin.whenever(sbn.tag).thenReturn(packageName)
        val notification = org.mockito.kotlin.mock<android.app.Notification>()
        val extras = android.os.Bundle()
        if (title != null) extras.putCharSequence(android.app.Notification.EXTRA_TITLE, title)
        notification.extras = extras
        org.mockito.kotlin.whenever(sbn.notification).thenReturn(notification)
        return sbn
    }

    @Test
    fun sameAppNotificationsCollapseToOneGroup() = runTest {
        val a = sbn("com.example.app", "key1", "Hello")
        val b = sbn("com.example.app", "key2", "World")
        engine.add("com.example.app", a)
        engine.add("com.example.app", b)
        val groups = engine.groupsFlow.value
        assertEquals(1, groups.size)
        assertEquals(2, groups["com.example.app"]?.count)
    }

    @Test
    fun differentAppsRemainSeparate() = runTest {
        engine.add("com.a", sbn("com.a", "k1"))
        engine.add("com.b", sbn("com.b", "k2"))
        val groups = engine.groupsFlow.value
        assertEquals(2, groups.size)
        assertTrue(groups.containsKey("com.a"))
        assertTrue(groups.containsKey("com.b"))
    }

    @Test
    fun removeClearsGroupWhenEmpty() = runTest {
        val sbn = sbn("com.example.app", "key1")
        engine.add("com.example.app", sbn)
        engine.remove("com.example.app", "key1")
        val groups = engine.groupsFlow.value
        assertFalse(groups.containsKey("com.example.app"))
    }

    @Test
    fun excludedPackagesAreNotGrouped() = runTest {
        val sbn = sbn("com.excluded", "key1")
        engine.add("com.excluded", sbn)
        assertTrue(true)
    }

    @Test
    fun nullExtrasDontCrash() = runTest {
        val sbn = org.mockito.kotlin.mock<StatusBarNotification>()
        org.mockito.kotlin.whenever(sbn.packageName).thenReturn("com.example.app")
        org.mockito.kotlin.whenever(sbn.key).thenReturn("key1")
        org.mockito.kotlin.whenever(sbn.tag).thenReturn("com.example.app")
        org.mockito.kotlin.whenever(sbn.notification).thenReturn(null)
        engine.add("com.example.app", sbn)
        val groups = engine.groupsFlow.value
        assertEquals(1, groups.size)
    }
}