package com.devpupu.notifgrouper

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmokeInstrumentedTest {
    @Test
    fun mainActivityLaunches() {
        val intent = android.content.Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            com.devpupu.notifgrouper.MainActivity::class.java
        ).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        InstrumentationRegistry.getInstrumentation().startActivitySync(intent)
    }
}