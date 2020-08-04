package com.emarsys.testUtil

import com.emarsys.Emarsys
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.kotlintest.shouldBe
import java.util.concurrent.CountDownLatch

object IntegrationTestUtils {

    @JvmStatic
    fun initializeFirebase() {
        val options: FirebaseOptions = FirebaseOptions.Builder()
                .setApplicationId("com.emarsys.sdk")
                .setProjectId("ems-mobile-engage-android-app")
                .setApiKey("AIzaSyC-SZ___dEWHfqsQL5viIQ_Z5WDw3NHBC4")
                .setApplicationId("1:1014228643013:android:dee9098abac0567e")
                .build()

        FirebaseApp.initializeApp(InstrumentationRegistry.getTargetContext(), options)
    }

    @JvmStatic
    fun doAppLogin(contactFieldValue: String = "test@test.com") {
        val latch = CountDownLatch(1)
        var errorCause: Throwable? = null
        Emarsys.setContact(contactFieldValue) {
            errorCause = it
            latch.countDown()
        }
        latch.await()
        errorCause shouldBe null
    }

    @JvmStatic
    fun doSetPushToken(pushToken: String = "integration_test_push_token") {
        val latch = CountDownLatch(1)
        var errorCause: Throwable? = null
        Emarsys.push.setPushToken(pushToken) {
            errorCause = it
            latch.countDown()
        }
        latch.await()
        errorCause shouldBe null
    }

    fun doLogin(contactFieldValue: String = "test@test.com", pushToken: String = "integration_test_push_token") {
        val latchForPushToken = CountDownLatch(2)
        var errorCause: Throwable? = null
        Emarsys.push.setPushToken(pushToken) { throwable ->
            errorCause = throwable
            latchForPushToken.countDown()
            Emarsys.setContact(contactFieldValue) {
                errorCause = it
                latchForPushToken.countDown()
            }
        }
        latchForPushToken.await()
        errorCause shouldBe null
    }
}