package com.emarsys.mobileengage.util

import com.emarsys.mobileengage.MobileEngageRequestContext
import com.emarsys.mobileengage.endpoint.Endpoint
import com.emarsys.testUtil.TimeoutUtils
import com.emarsys.testUtil.mockito.whenever
import io.kotlintest.shouldBe
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito

class RequestUrlUtilsTest {
    companion object {
        const val APPLICATION_CODE = "app_code"
    }

    private lateinit var mockRequestContext: MobileEngageRequestContext

    @Rule
    @JvmField
    val timeout: TestRule = TimeoutUtils.timeoutRule

    @Before
    fun setUp() {
        mockRequestContext = Mockito.mock(MobileEngageRequestContext::class.java).apply {
            whenever(applicationCode).thenReturn(APPLICATION_CODE)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCreateSetPushTokenUrl_requestContext_mustNotBeNull() {
        RequestUrlUtils.createSetPushTokenUrl(null)
    }

    @Test
    fun testCreateSetPushTokenUrl() {
        val url = RequestUrlUtils.createSetPushTokenUrl(mockRequestContext)
        url shouldBe "https://me-client.eservice.emarsys.net/v3/apps/$APPLICATION_CODE/client/push-token"
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCreateRemovePushTokenUrl_requestContext_mustNotBeNull() {
        RequestUrlUtils.createRemovePushTokenUrl(null)
    }

    @Test
    fun testCreateRemovePushTokenUrl() {
        val url = RequestUrlUtils.createRemovePushTokenUrl(mockRequestContext)
        url shouldBe "https://me-client.eservice.emarsys.net/v3/apps/$APPLICATION_CODE/client/push-token"
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCreateTrackDeviceInfoUrl_requestContext_mustNotBeNull() {
        RequestUrlUtils.createTrackDeviceInfoUrl(null)
    }

    @Test
    fun testCreateTrackDeviceInfoUrl() {
        val url = RequestUrlUtils.createTrackDeviceInfoUrl(mockRequestContext)
        url shouldBe "https://me-client.eservice.emarsys.net/v3/apps/$APPLICATION_CODE/client"
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCreateSetContactUrl_requestContext_mustNotBeNull() {
        RequestUrlUtils.createSetContactUrl(null)
    }

    @Test
    fun testCreateSetContactUrl() {
        val url = RequestUrlUtils.createSetContactUrl(mockRequestContext)

        url shouldBe "https://me-client.eservice.emarsys.net/v3/apps/$APPLICATION_CODE/client/contact"
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCreateTrackCustomEventUrl_requestContext_mustNotBeNull() {
        RequestUrlUtils.createCustomEventUrl(null)
    }

    @Test
    fun testCreateTrackCustomEventUrl() {
        val url = RequestUrlUtils.createCustomEventUrl(mockRequestContext)

        url shouldBe "https://mobile-events.eservice.emarsys.net/v3/apps/$APPLICATION_CODE/client/events"
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCreateRefreshContactTokenUrl_requestContext_mustNotBeNull() {
        RequestUrlUtils.createRefreshContactTokenUrl(null)
    }

    @Test
    fun testCreateRefreshContactTokenUrl() {
        val url = RequestUrlUtils.createRefreshContactTokenUrl(mockRequestContext)

        url shouldBe "https://me-client.eservice.emarsys.net/v3/apps/$APPLICATION_CODE/client/contact-token"
    }

    @Test(expected = IllegalArgumentException::class)
    fun testIsMobileEngageUrl_url_mustNotBeNull() {
        RequestUrlUtils.isMobileEngageV3Url(null)
    }

    @Test
    fun testIsMobileEngageUrl_true_whenItIsMobileEngageClient() {
        val result = RequestUrlUtils.isMobileEngageV3Url(Endpoint.ME_V3_CLIENT_BASE)

        result shouldBe true
    }

    @Test
    fun testIsMobileEngageUrl_true_whenItIsMobileEngageEvent() {
        val result = RequestUrlUtils.isMobileEngageV3Url(Endpoint.ME_V3_EVENT_BASE)

        result shouldBe true
    }

    @Test
    fun testIsMobileEngageUrl_false_whenItIsNotMobileEngage() {
        val result = RequestUrlUtils.isMobileEngageV3Url("https://not-mobile-engage.com")

        result shouldBe false
    }

    @Test(expected = IllegalArgumentException::class)
    fun testIsCustomEvent_V3_requestModel_mustNotBeNull() {
        RequestUrlUtils.isCustomEvent_V3(null)
    }

    @Test
    fun testIsCustomEvent_V3_true_whenItIsCustomEventV3Event() {
        val result = RequestUrlUtils.isCustomEvent_V3(Endpoint.ME_V3_EVENT_BASE)

        result shouldBe true
    }

    @Test
    fun testIsCustomEvent_V3_false_whenItIsNotCustomEventV3Event() {
        val result = RequestUrlUtils.isCustomEvent_V3(Endpoint.ME_V3_CLIENT_BASE)

        result shouldBe false
    }

    @Test(expected = IllegalArgumentException::class)
    fun testIsRefreshContactTokenUrl_url_mustNotBeNull() {
        RequestUrlUtils.isRefreshContactTokenUrl(null)
    }

    @Test
    fun testIsRefreshContactTokenUrl_shouldBeTrue() {
        val result = RequestUrlUtils.isRefreshContactTokenUrl("https://me-client.eservice.emarsys.net/v3/apps/$APPLICATION_CODE/client/contact-token")

        result shouldBe true
    }

    @Test
    fun testIsRefreshContactTokenUrl_shouldBeFalse() {
        val result = RequestUrlUtils.isRefreshContactTokenUrl("https://not-refresh-token.com")

        result shouldBe false
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCreateEventUrl_V2_shouldNotAcceptNull() {
        RequestUrlUtils.createEventUrl_V2(null)
    }

    @Test
    fun testCreateEventUrl_V2_shouldReturnTheCorrectEventUrl() {
        val url = RequestUrlUtils.createEventUrl_V2("my-custom-event")
        val expected = "https://push.eservice.emarsys.net/api/mobileengage/v2/events/my-custom-event"
        url shouldBe expected
    }

}