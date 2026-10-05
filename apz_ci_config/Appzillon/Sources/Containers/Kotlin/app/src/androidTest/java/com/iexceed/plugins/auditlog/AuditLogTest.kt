package com.iexceed.plugins.auditlog

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iexceed.plugins.utils.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuditLogTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Test
    fun test_makeString() {
        val auditLog = AuditLog
        auditLog.makeString("ABC","action")
        Assert.assertEquals("ABC",auditLog.pluginName)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_sendToJSON() {
        testCoroutineRule.runBlockingTest {
            val auditLog = AuditLog
            auditLog.sendToJSON()
            Assert.assertNotEquals("",auditLog.auditMessage)
            Assert.assertNotNull(auditLog.auditMessage)
        }
    }
}