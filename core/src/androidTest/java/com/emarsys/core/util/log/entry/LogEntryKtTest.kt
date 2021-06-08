package com.emarsys.core.util.log.entry

import com.emarsys.core.util.log.LogLevel
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.kotlintest.shouldBe
import org.junit.Test

class LogEntryKtTest {

    @Test
    fun testToData() {
        val logEntry: LogEntry = mock {
            on { data } doReturn mapOf(
                "testKey" to "testValue",
                "testKey2" to "testValue2"
            )
        }
        val result = logEntry.toData(LogLevel.INFO, "testThreadName", "flutter")

        result shouldBe mapOf(
            "testKey" to "testValue",
            "testKey2" to "testValue2",
            "level" to "INFO",
            "thread" to "testThreadName",
            "wrapper" to "flutter"
        )
    }

    @Test
    fun testToData_shouldExcludeWrapper_whenWrapperInfo_isNull() {
        val logEntry: LogEntry = mock {
            on { data } doReturn mapOf(
                "testKey" to "testValue",
                "testKey2" to "testValue2"
            )
        }
        val result = logEntry.toData(LogLevel.INFO, "testThreadName", null)

        result shouldBe mapOf(
            "testKey" to "testValue",
            "testKey2" to "testValue2",
            "level" to "INFO",
            "thread" to "testThreadName"
        )
    }
}