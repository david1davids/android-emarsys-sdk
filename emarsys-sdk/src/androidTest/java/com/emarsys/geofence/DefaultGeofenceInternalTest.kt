package com.emarsys.geofence

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import com.emarsys.core.permission.PermissionChecker
import com.emarsys.core.request.RequestManager
import com.emarsys.core.request.model.RequestModel
import com.emarsys.core.response.ResponseModel
import com.emarsys.mobileengage.geofence.DefaultGeofenceInternal
import com.emarsys.mobileengage.geofence.GeofenceInternal
import com.emarsys.mobileengage.geofence.GeofenceResponseMapper
import com.emarsys.mobileengage.request.MobileEngageRequestModelFactory
import com.emarsys.testUtil.FakeRequestManager
import com.emarsys.testUtil.TimeoutUtils
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class DefaultGeofenceInternalTest {

    @Rule
    @JvmField
    val timeout: TestRule = TimeoutUtils.timeoutRule

    private lateinit var mockFetchGeofenceRequestModel: RequestModel
    private lateinit var mockResponseModel: ResponseModel
    private lateinit var mockRequestModelFactory: MobileEngageRequestModelFactory
    private lateinit var fakeRequestManager: RequestManager
    private lateinit var mockRequestManager: RequestManager
    private lateinit var mockLocationManager: LocationManager
    private lateinit var mockGeofenceResponseMapper: GeofenceResponseMapper
    private lateinit var geofenceInternal: GeofenceInternal
    private lateinit var mockPermissionChecker: PermissionChecker

    @Before
    fun setUp() {

        mockFetchGeofenceRequestModel = mock()
        mockResponseModel = mock()
        mockRequestModelFactory = mock {
            on { createFetchGeofenceRequest() } doReturn mockFetchGeofenceRequestModel
        }
        fakeRequestManager = FakeRequestManager(FakeRequestManager.ResponseType.SUCCESS, mockResponseModel)
        mockRequestManager = mock()
        mockGeofenceResponseMapper = mock()
        mockPermissionChecker = mock()
        mockLocationManager = mock()

        geofenceInternal = DefaultGeofenceInternal(mockRequestModelFactory, mockRequestManager, mockGeofenceResponseMapper, mockPermissionChecker, mockLocationManager)
    }

    @Test
    fun testFetchGeofences_shouldSendRequest_viaRequestManager_submitNow() {
        geofenceInternal.fetchGeofences()

        verify(mockRequestManager).submitNow(any(), any())
    }

    @Test
    fun testFetchGeofences_callsMapOnResponseMapper_onSuccess() {
        geofenceInternal = DefaultGeofenceInternal(mockRequestModelFactory, fakeRequestManager, mockGeofenceResponseMapper, mockPermissionChecker, mockLocationManager)

        geofenceInternal.fetchGeofences()

        verify(mockGeofenceResponseMapper).map(mockResponseModel)
    }

    @Test
    fun testEnable_checksForLocationPermissions_throughPermissionChecker() {
        geofenceInternal.enable(null)

        verify(mockPermissionChecker).checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @Test
    fun testEnable_fetchLastKnownLocation_fromLocationManager_whenPermissionGranted() {
        whenever(mockPermissionChecker.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)).thenReturn(PackageManager.PERMISSION_GRANTED)

        geofenceInternal.enable(null)

        verify(mockPermissionChecker).checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        verify(mockLocationManager).getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }
}