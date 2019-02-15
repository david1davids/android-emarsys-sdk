package com.emarsys.mobileengage.deeplink;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.emarsys.core.api.result.CompletionListener;
import com.emarsys.core.device.DeviceInfo;
import com.emarsys.core.provider.timestamp.TimestampProvider;
import com.emarsys.core.provider.uuid.UUIDProvider;
import com.emarsys.core.request.RequestManager;
import com.emarsys.core.request.model.RequestModel;
import com.emarsys.core.storage.Storage;
import com.emarsys.mobileengage.MobileEngageInternal_V3_Old;
import com.emarsys.mobileengage.RequestContext;
import com.emarsys.mobileengage.storage.AppLoginStorage;
import com.emarsys.mobileengage.storage.MeIdSignatureStorage;
import com.emarsys.mobileengage.storage.MeIdStorage;
import com.emarsys.testUtil.TimeoutUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeepLinkInternalTest {

    private Activity mockActivity;
    private DeepLinkInternal deepLinkInternal;
    private RequestManager manager;
    private RequestContext requestContext;
    private TimestampProvider timestampProvider;
    private UUIDProvider uuidProvider;

    @Rule
    public TestRule timeout = TimeoutUtils.getTimeoutRule();


    @Before
    public void init() {
        mockActivity = mock(Activity.class, Mockito.RETURNS_DEEP_STUBS);

        manager = mock(RequestManager.class);

        timestampProvider = mock(TimestampProvider.class);
        uuidProvider = mock(UUIDProvider.class);
        when(uuidProvider.provideId()).thenReturn("REQUEST_ID");
        requestContext = new RequestContext(
                "",
                "",
                1,
                mock(DeviceInfo.class),
                mock(AppLoginStorage.class),
                mock(MeIdStorage.class),
                mock(MeIdSignatureStorage.class),
                timestampProvider,
                uuidProvider,
                mock(Storage.class),
                mock(Storage.class));

        deepLinkInternal = new DeepLinkInternal(manager, requestContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_requestManagerMustNotBeNull() {
        new DeepLinkInternal(null, requestContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_requestContextMustNotBeNull() {
        new DeepLinkInternal(manager, null);
    }

    @Test
    public void testTrackDeepLink_requestManagerCalled_withCorrectRequestModel() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://demo-mobileengage.emarsys.net/something?fancy_url=1&ems_dl=1_2_3_4_5"));

        Map<String, Object> payload = new HashMap<>();
        payload.put("ems_dl", "1_2_3_4_5");

        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent",
                String.format("Mobile Engage SDK %s Android %s", MobileEngageInternal_V3_Old.MOBILEENGAGE_SDK_VERSION, Build.VERSION.SDK_INT));

        RequestModel expected = new RequestModel.Builder(timestampProvider, uuidProvider)
                .url("https://deep-link.eservice.emarsys.net/api/clicks")
                .headers(headers)
                .payload(payload)
                .build();

        ArgumentCaptor<RequestModel> captor = ArgumentCaptor.forClass(RequestModel.class);

        deepLinkInternal.trackDeepLinkOpen(mockActivity, intent, null);

        verify(manager).submit(captor.capture(), (CompletionListener) isNull());

        RequestModel result = captor.getValue();
        assertRequestModels(expected, result);
    }

    @Test
    public void testTrackDeepLink_requestManagerCalled_withCorrectCompletionHandler() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://demo-mobileengage.emarsys.net/something?fancy_url=1&ems_dl=1_2_3_4_5"));

        CompletionListener completionListener = mock(CompletionListener.class);

        deepLinkInternal.trackDeepLinkOpen(mockActivity, intent, completionListener);

        verify(manager).submit(any(RequestModel.class), eq(completionListener));
    }

    @Test
    public void testTrackDeepLink_setsClickedFlag_onIntentBundle() {
        Intent originalIntent = mock(Intent.class);
        when(mockActivity.getIntent()).thenReturn(originalIntent);

        Intent currentIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://demo-mobileengage.emarsys.net/something?fancy_url=1&ems_dl=1_2_3_4_5"));

        deepLinkInternal.trackDeepLinkOpen(mockActivity, currentIntent, null);

        verify(originalIntent).putExtra("ems_deep_link_tracked", true);
    }

    @Test
    public void testTrackDeepLink_doesNotCallRequestManager_whenTrackedFlagIsSet() {
        Intent intentFromActivity = mock(Intent.class);
        when(mockActivity.getIntent()).thenReturn(intentFromActivity);
        when(intentFromActivity.getBooleanExtra("ems_deep_link_tracked", false)).thenReturn(true);

        Intent currentIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://demo-mobileengage.emarsys.net/something?fancy_url=1&ems_dl=1_2_3_4_5"));

        deepLinkInternal.trackDeepLinkOpen(mockActivity, currentIntent, null);

        verify(manager, times(0)).submit(any(RequestModel.class), (CompletionListener) isNull());
    }

    @Test
    public void testTrackDeepLink_doesNotCallRequestManager_whenDataIsNull() {
        Intent intent = new Intent();

        deepLinkInternal.trackDeepLinkOpen(mockActivity, intent, null);

        verify(manager, times(0)).submit(any(RequestModel.class), (CompletionListener) isNull());
    }

    @Test
    public void testTrackDeepLink_doesNotCallRequestManager_whenUriDoesNotContainEmsDlQueryParameter() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://demo-mobileengage.emarsys.net/something?fancy_url=1&other=1_2_3_4_5"));

        deepLinkInternal.trackDeepLinkOpen(mockActivity, intent, null);

        verify(manager, times(0)).submit(any(RequestModel.class), (CompletionListener) isNull());
    }

    private void assertRequestModels(RequestModel expected, RequestModel result) {
        assertEquals(expected.getUrl(), result.getUrl());
        assertEquals(expected.getMethod(), result.getMethod());
        assertEquals(expected.getPayload(), result.getPayload());
        assertEquals(expected.getHeaders(), result.getHeaders());
    }
}