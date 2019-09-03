package com.emarsys.mobileengage.notification.command

import android.app.Application
import android.content.Intent
import android.os.Build.VERSION_CODES.KITKAT
import android.os.Bundle
import android.os.Handler
import androidx.test.filters.SdkSuppress
import com.emarsys.core.activity.ActivityLifecycleWatchdog
import com.emarsys.core.concurrency.CoreSdkHandlerProvider
import com.emarsys.core.di.DependencyInjection
import com.emarsys.core.provider.timestamp.TimestampProvider
import com.emarsys.core.util.FileUtils
import com.emarsys.mobileengage.di.MobileEngageDependencyContainer
import com.emarsys.mobileengage.iam.InAppPresenter
import com.emarsys.mobileengage.iam.PushToInAppAction
import com.emarsys.testUtil.InstrumentationRegistry
import com.emarsys.testUtil.TimeoutUtils
import com.emarsys.testUtil.mockito.whenever
import io.kotlintest.shouldBe
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import java.io.File
import java.util.concurrent.CountDownLatch

@SdkSuppress(minSdkVersion = KITKAT)
class PreloadedInappHandlerCommandTest {
    companion object {
        private const val URL = "https://www.google.com"
    }

    private lateinit var mockDependencyContainer: MobileEngageDependencyContainer
    private lateinit var mockActivityLifecycleWatchdog: ActivityLifecycleWatchdog
    private lateinit var mockCoreSdkHandler: Handler
    private lateinit var fileUrl: String

    @Rule
    @JvmField
    val timeout: TestRule = TimeoutUtils.timeoutRule

    private val application: Application
        get() = InstrumentationRegistry.getTargetContext().applicationContext as Application


    @Before
    fun setUp() {
        fileUrl = InstrumentationRegistry.getTargetContext().applicationContext.cacheDir.absolutePath + "/test.file"

        mockCoreSdkHandler = CoreSdkHandlerProvider().provideHandler()

        mockActivityLifecycleWatchdog = mock(ActivityLifecycleWatchdog::class.java)

        mockDependencyContainer = mock(MobileEngageDependencyContainer::class.java).apply {
            whenever(activityLifecycleWatchdog).thenReturn(mockActivityLifecycleWatchdog)
            whenever(inAppPresenter).thenReturn(mock(InAppPresenter::class.java))
            whenever(timestampProvider).thenReturn(mock(TimestampProvider::class.java))
            whenever(coreSdkHandler).thenReturn(mockCoreSdkHandler)
        }

    }

    @After
    fun tearDown() {
        application.unregisterActivityLifecycleCallbacks(mockActivityLifecycleWatchdog)
        mockCoreSdkHandler.looper.quitSafely()
        DependencyInjection.tearDown()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testHandlePreloadedInAppMessage_intentMustNotBeNull() {
        PreloadedInappHandlerCommand(null, mockDependencyContainer)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testHandlePreloadedInAppMessage_dependencyContainerMustNotBeNull() {
        PreloadedInappHandlerCommand(Intent(), null)
    }

    @Test
    fun testHandlePreloadedInAppMessage_shouldCallAddTriggerOnActivityAction_whenFileUrlIsAvailable() {
        FileUtils.writeToFile("test", fileUrl)

        val inapp = JSONObject().apply {
            put("campaignId", "campaignId")
            put("fileUrl", fileUrl)
        }
        val ems = JSONObject().apply {
            put("inapp", inapp.toString())
        }
        val payload = Bundle().apply {
            putString("ems", ems.toString())
        }
        val intent = Intent().apply {
            putExtra("payload", payload)
        }

        PreloadedInappHandlerCommand(intent, mockDependencyContainer).run()

        waitForEventLoopToFinish(mockCoreSdkHandler)

        verify(mockActivityLifecycleWatchdog).addTriggerOnActivityAction(any(PushToInAppAction::class.java))
    }

    @Test
    fun testHandlePreloadedInAppMessage_shouldCallAddTriggerOnActivityAction_whenFileUrlIsAvailableButTheFileIsMissing() {
        FileUtils.writeToFile("test", fileUrl)
        File(fileUrl).delete()

        val inapp = JSONObject().apply {
            put("campaignId", "campaignId")
            put("fileUrl", fileUrl)
            put("url", URL)
        }
        val ems = JSONObject().apply {
            put("inapp", inapp.toString())
        }
        val payload = Bundle().apply {
            putString("ems", ems.toString())
        }
        val intent = Intent().apply {
            putExtra("payload", payload)
        }

        PreloadedInappHandlerCommand(intent, mockDependencyContainer).run()

        waitForEventLoopToFinish(mockCoreSdkHandler)

        verify(mockActivityLifecycleWatchdog).addTriggerOnActivityAction(any(PushToInAppAction::class.java))
    }

    @Test
    fun testHandlePreloadedInAppMessage_shouldCallAddTriggerOnActivityAction_whenFileUrlIsNotAvailable_butUrlIsAvailable() {
        val inapp = JSONObject().apply {
            put("campaignId", "campaignId")
            put("url", URL)
        }
        val ems = JSONObject().apply {
            put("inapp", inapp.toString())
        }
        val payload = Bundle().apply {
            putString("ems", ems.toString())
        }
        val intent = Intent().apply {
            putExtra("payload", payload)
        }

        PreloadedInappHandlerCommand(intent, mockDependencyContainer).run()

        waitForEventLoopToFinish(mockCoreSdkHandler)

        verify(mockActivityLifecycleWatchdog).addTriggerOnActivityAction(any(PushToInAppAction::class.java))
    }

    @Test
    fun testHandlePreloadedInAppMessage_shouldDeleteFile_afterPushToInAppActionIsScheduled() {
        FileUtils.writeToFile("test", fileUrl)

        val inapp = JSONObject().apply {
            put("campaignId", "campaignId")
            put("fileUrl", fileUrl)
        }
        val ems = JSONObject().apply {
            put("inapp", inapp.toString())
        }
        val payload = Bundle().apply {
            putString("ems", ems.toString())
        }
        val intent = Intent().apply {
            putExtra("payload", payload)
        }

        File(fileUrl).exists() shouldBe true

        PreloadedInappHandlerCommand(intent, mockDependencyContainer).run()

        waitForEventLoopToFinish(mockCoreSdkHandler)

        verify(mockActivityLifecycleWatchdog).addTriggerOnActivityAction(any(PushToInAppAction::class.java))

        File(fileUrl).exists() shouldBe false
    }

    @Test
    fun testHandlePreloadedInAppMessage_shouldNotScheduleInAppDisplay_ifInAppProperty_isMissing() {
        val ems = JSONObject()
        val payload = Bundle().apply {
            putString("ems", ems.toString())
        }
        val intent = Intent().apply {
            putExtra("payload", payload)
        }

        PreloadedInappHandlerCommand(intent, mockDependencyContainer).run()

        waitForEventLoopToFinish(mockCoreSdkHandler)

        verifyZeroInteractions(mockActivityLifecycleWatchdog)
    }

    private fun waitForEventLoopToFinish(handler: Handler) {
        val latch = CountDownLatch(1)
        handler.post { latch.countDown() }

        latch.await()
    }
}